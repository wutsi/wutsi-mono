function WutsiCbz1(url, numberOfPages, location, trackCallback, relocateCallback) {
    this.url = url;
    this.numberOfPages = numberOfPages;
    this.location = location;
    this.trackCallback = trackCallback;
    this.relocateCallback = relocateCallback;

    this.render = function () {
        let innerHTML = '';
        for (let i = 1; i < this.numberOfPages; i++) {
            const src = this.url + '/pages/' + i;
            innerHTML += "<img loading='lazy' class='page' src='" + src + "'/>"
        }
        document.getElementById('viewer').innerHTML = innerHTML;

        this.relocate(0)
    };

    this.track = function (event, value) {
        if (this.trackCallback) {
            this.trackCallback(event, value);
        }
    }

    this.relocate = function (number) {
        this.location = number;
        if (this.relocateCallback) {
            this.relocateCallback(location, percent);
        }
    }
}
