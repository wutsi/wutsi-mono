function WutsiCbz(url, location, trackCallback, relocateCallback) {
    this.url = url;
    this.location = location;
    this.trackCallback = trackCallback;
    this.relocateCallback = relocateCallback;

    this.render = function () {
        this.relocate(1)
    };

    this.track = function (event, value) {
        if (this.trackCallback) {
            this.trackCallback(event, value);
        }
    }

    this.relocate = function (number) {
        const src = this.url + '/pages/' + number;
        document.getElementById('viewer').innerHTML = "<img class='page' src='" + src + "'/>";
        if (this.relocateCallback) {
            this.relocateCallback(location, percent);
        }
    }
}
