function WutsiEpubJS(url, location, trackCallback, relocateCallback, buyCallback) {
    this.url = url;
    this.location = location;
    this.trackCallback = trackCallback;
    this.relocateCallback = relocateCallback;
    this.buyCallback = buyCallback;

    this.track = function (event, value) {
        if (this.trackCallback) {
            this.trackCallback(event, value);
        }
    }

    this.relocate = function (location, percent) {
        if (this.relocateCallback) {
            this.relocateCallback(location.start.cfi, percent);
        }
    }

    this.buy = function () {
        if (this.buyCallback) {
            this.buyCallback();
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
                html: {
                    'font-family': "'PT Sans', sans-serif !important",
                    'background': wutsi.is_dark_mode() ? '#2a2e33 !important' : '#fff !important',
                    'color': wutsi.is_dark_mode() ? '#fff !important' : '#000 !important',
                },
                body: {
                    'margin': '0 !important',
                    'padding': '3em 3em 3em 3em !important',
                },
                h1: {
                    'font-size': '3em !important',
                    'margin': '0 0 1em 0 !important',
                    'color': wutsi.is_dark_mode() ? '#fff !important' : '#000 !important',
                    'text-transform': 'uppercase',
                    'font-weight': 'bold',
                },
                h2: {
                    'font-size': '2em !important',
                    'margin': '0 0 1em 0 !important',
                    'color': wutsi.is_dark_mode() ? '#fff !important' : '#000 !important',
                    'text-transform': 'uppercase',
                    'font-weight': 'bold',
                },
                'p, div': {
                    'font-size': '20px !important',
                    'font-weight': 'normal !important',
                    'line-height': '25px !important',
                    'margin': '0 0 1em 0 !important',
                    'text-align': 'left !important',
                    'font-family': "'PT Sans', sans-serif !important",
                    'color': wutsi.is_dark_mode() ? '#fff !important' : '#000 !important',
                    'text-indent': '-0.5pt !important',
                },
                img: {
                    'max-width': '90%',
                },

                /* convert.io style */
                '.calibre1': {
                    'display': 'block !important',
                    'padding': '0 !important',
                },
                '.bullet_': {
                    'display': 'inline !important',
                    'padding-right': '1em !important',
                    'color': 'inherit !important',
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

            // Buy
            const buy = document.getElementById("btn-buy");
            if (buy) {
                buy.addEventListener("click", function () {
                    me.buy()
                }, false);
            }

            // Table of content
            const toc = document.getElementById("btn-toc");
            if (toc != null) {
                toc.addEventListener("click", function () {
                    const toc = document.getElementById("toc");
                    if (toc.style.visibility === 'hidden') {
                        toc.style.visibility = 'visible';
                    } else {
                        toc.style.visibility = 'hidden';
                    }
                }, false);
            }
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
            next.style.visibility = location.atEnd ? "hidden" : "visible";

            const prev = document.getElementById("prev");
            prev.style.visibility = location.atStart ? "hidden" : "visible";

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
