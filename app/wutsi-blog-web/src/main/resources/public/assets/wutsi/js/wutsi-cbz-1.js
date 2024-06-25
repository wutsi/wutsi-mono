function WutsiCbz1(url, numberOfPages, location, trackCallback, relocateCallback) {
    this.url = url;
    this.numberOfPages = numberOfPages;
    this.location = location;
    this.trackCallback = trackCallback;
    this.relocateCallback = relocateCallback;

    this.render = function () {
        // Content
        let innerHTML = '';
        for (let i = 1; i < this.numberOfPages; i++) {
            const src = this.url + '/pages/' + i;
            innerHTML += "<img loading='lazy' class='page' src='" + src + "'/>"
        }
        document.getElementById('viewer').innerHTML = innerHTML;

        // Initial position
        this.relocate(this.location ? this.location : 0);

        const me = this;
        window.addEventListener('scroll', function () {
            console.log('>>> scrolling...');
            // See https://stackoverflow.com/questions/2387136/cross-browser-method-to-determine-vertical-scroll-percentage-in-javascript
            const scrollPercent = ((document.documentElement.scrollTop + document.body.scrollTop) /
                (document.documentElement.scrollHeight - document.documentElement.clientHeight) * 100);
            console.log('>>> scroll-percent', scrollPercent);
            if (scrollPercent % 5 === 0) {
                me.on_relocate(scrollPercent);
            }
        });

        // Start
        this.track("playstart");
    };

    this.track = function (event, value) {
        if (this.trackCallback) {
            this.trackCallback(event, value);
        }
    }

    this.relocate = function (number) {
        $(window).scrollTop(number);
        this.onRelocate(number);
    }

    this.on_relocate = function (number) {
        this.location = number;
        if (this.relocateCallback) {
            this.relocateCallback(number, number);
        }
    }
}
