function WutsiCbz1(url, numberOfPages, location, trackCallback, relocateCallback) {
    this.url = url;
    this.numberOfPages = numberOfPages;
    this.location = location;
    this.trackCallback = trackCallback;
    this.relocateCallback = relocateCallback;

    this.render = function () {
        // Content
        let innerHTML = '';
        for (let i = 1; i <= this.numberOfPages; i++) {
            const src = this.url + '/pages/' + i;
            innerHTML += "<img loading='lazy' class='page' src='" + src + "'/>"
        }
        document.getElementById('viewer').innerHTML = innerHTML;

        // Track scrolling
        const me = this;
        window.addEventListener('scroll', function () {
            // See https://stackoverflow.com/questions/2387136/cross-browser-method-to-determine-vertical-scroll-percentage-in-javascript
            const percent = (100 * (document.documentElement.scrollTop + document.body.scrollTop) /
                (document.documentElement.scrollHeight - document.documentElement.clientHeight));
            const scrollPercent = parseInt(percent, 10)
            let currentPercent = 0
            if ((scrollPercent !== currentPercent) && (scrollPercent % 10 === 0 || scrollPercent > 95)) {
                me.on_relocated(scrollPercent);
                currentPercent = scrollPercent;
            }
        });

        // Start
        this.track("playstart");

        // Initial position
        // console.log('>>> location=' + location, ' scrollHeight=' + document.documentElement.scrollHeight);
        // if (location) {
        //     const scrollY = document.documentElement.scrollHeight * location / 100;
        //     console.log('>>> scrollY=' + scrollY);
        //     window.scrollTo(0, scrollY);
        // }
    };

    this.track = function (event, value) {
        if (this.trackCallback) {
            this.trackCallback(event, value);
        }
    }

    this.relocate = function (number) {
        if (this.relocateCallback) {
            this.relocateCallback(number, number);
        }
    }

    this.on_relocated = function (number) {
        this.location = number;

        this.relocate(number);
        this.track('playing', number);
    }
}
