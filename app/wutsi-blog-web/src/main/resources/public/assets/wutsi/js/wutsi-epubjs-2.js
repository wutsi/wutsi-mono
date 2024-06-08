function WutsiEpubJS(url, location, trackCallback, relocateCallback) {
    this.url = url;
    this.location = location;
    this.trackCallback = trackCallback;
    this.relocateCallback = relocateCallback;

    this.track = function (event, value) {
        if (this.trackCallback) {
            this.trackCallback(event, value);
        }
    }

    this.relocate = function (location, percent) {
        if (this.relocateCallback) {
            this.relocateCallback(location, percent);
        }
    }

    this.render = function () {
        const me = this;
        const book = ePub(this.url);
        const rendition = book.renderTo(
            'viewer',
            {
                method: "default",
                flow: "paginated",
                spread: "none",
                width: "100%",
                height: "100%",
                resizeOnOrientationChange: true
            }
        );

        rendition.themes.default(
            {
                body: {
                    'margin': '0 !important',
                    'padding': '3em !important',
                    'font-family': "'PT Sans', sans-serif !important",
                    'color': 'var(--text-color)',
                },
                h1: {
                    'font-size': '3em !important',
                    'margin': '0 0 1em 0 !important',
                },
                h2: {
                    'font-size': '2em !important',
                    'margin': '0 0 1em 0 !important',
                },
                p: {
                    'font-size': '20px !important',
                    'font-weight': 'normal !important',
                    'line-height': '25px !important',
                    'margin': '0 0 1em 0 !important',
                    'text-align': 'left !important',
                    'font-family': "'PT Sans', sans-serif !important",
                    'color': wutsi.is_dark_mode() ? '#fff !important' : '#000 !important',
                    'text-indent': '-0.5pt !important',
                },
                'p span': {
                    'font-size': '20px !important',
                    'line-height': '25px !important',
                    'white-space': 'normal !important',
                    'text-align': 'left !important',
                    'font-family': "'PT Sans', sans-serif !important"
                },
                img: {
                    'max-width': '90%',
                },

                '.bullet_': {
                    'color': wutsi.is_dark_mode() ? '#fff !important' : '#000 !important',
                    'font-size': '20px !important'
                },

                '.calibre': {
                    'color': wutsi.is_dark_mode() ? '#fff' : '#000'
                },

                'div.calibre1': {
                    'display': 'block',
                    'font-size': '20px !important',
                    'line-height': '25px !important',
                    'margin': '0 0 1em 0 !important',
                    'text-align': 'left !important',
                    'font-family': "'PT Sans', sans-serif !important"
                },
                'div.calibre1 .block_4': {
                    'display': 'block',
                    'font-size': 'inherit !important',
                    'margin': '0 0 1em 0 !important',
                    'line-height': 'inherit !important'
                },
                'div.calibre1 .block_5': {
                    'display': 'block',
                    'font-size': 'inherit !important',
                    'line-height': 'inherit !important',
                    'margin': '0 !important',
                },

                'div.calibre2': {
                    'display': 'block !important',
                    'padding': '0 0 0 0 !important'
                },
                'div.calibre2 div': {
                    'display': 'block !important',
                    'font-size': '20px !important',
                    'font-weight': 'normal !important',
                    'line-height': '25px !important',
                    'margin': '0 0 1em 0 !important',
                    'text-align': 'left !important',
                    'font-family': "'PT Sans', sans-serif !important",
                    'color': wutsi.is_dark_mode() ? '#fff !important' : '#000 !important',
                    'text-indent': '-0.5pt !important',
                },
                'div.calibre2 div span': {
                    'display': 'inline !important',
                }
            }
        );

        if (this.location && this.location.length > 0) {
            rendition.display(this.location);
        } else {
            rendition.display();
        }

        book.ready.then(function () {
            // Tracking
            me.track("playstart");
            const startTime = (new Date()).getTime();
            window.addEventListener('beforeunload', function () {
                const durationMillis = (new Date()).getTime() - startTime;
                me.track('playend', durationMillis);
            });

            // Next button
            const next = document.getElementById("next");
            next.addEventListener("click", function () {
                rendition.next();
            }, false);

            // Previous button
            const prev = document.getElementById("prev");
            prev.addEventListener("click", function () {
                rendition.prev();
            }, false);

            // Table of content
            document.getElementById("btn-toc").addEventListener("click", function () {
                const toc = document.getElementById("toc");
                if (toc.style.visibility === 'hidden') {
                    toc.style.visibility = 'visible';
                } else {
                    toc.style.visibility = 'hidden';
                }
            }, false);
        });

        book.loaded.navigation.then(function (toc) {
            const root = document.getElementById("toc");

            toc.forEach(function (chapter) {
                // Create DIV
                const a = document.createElement("div");
                a.className = 'toc-item';
                a.setAttribute("data-href", chapter.href);
                a.innerHTML = chapter.label;
                root.appendChild(a);

                // Click
                a.addEventListener('click', function () {
                    const href = this.getAttribute('data-href');
                    rendition.display(href);
                    root.style.visibility = 'hidden';
                });
            });
        });

        rendition.on('relocated', function (location) {
            console.log('relocated', location);

            // Update location
            const index = location.start.index + 1;
            const total = book.locations.spine.items.length;
            document.getElementById('pagination').innerText = index + " of " + total;

            // Update navigation
            const next = document.getElementById("next");
            if (location.atEnd) {
                next.style.visibility = "hidden";
            } else {
                next.style.visibility = "visible";
            }

            const prev = document.getElementById("prev");
            if (location.atStart) {
                prev.style.visibility = "hidden";
            } else {
                prev.style.visibility = "visible";
            }

            // Store
            let readPercentage = 0;
            if (total > 0) {
                readPercentage = location.atEnd ? 100 : Math.min(99, 100 * index / total);
            }

            // Track
            me.relocate(location, readPercentage);
            me.track("playing", index)
        });
    };
}
