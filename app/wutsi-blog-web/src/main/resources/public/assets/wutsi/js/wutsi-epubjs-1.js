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
                    'font-family': "'PT Sans', sans-serif !important"
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
                    'line-height': '25px !important',
                    'margin': '0 0 1em 0 !important',
                    'text-align': 'left !important',
                    'font-family': "'PT Sans', sans-serif !important"
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
                const div = document.createElement("div");
                root.appendChild(div);

                const a = document.createElement("a")
                a.setAttribute("href", "javascript: relocate('" + chapter.href + "')");
                a.text = chapter.label
                div.appendChild(a);
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
            me.track("playing", index)
        });
    };
}
