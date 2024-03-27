document.addEventListener("DOMContentLoaded", function () {
    $('.tweet').each(function (t, tweet) {

        var id = jQuery(this).attr('data-id');
        twttr.widgets.createTweet(
            id, tweet,
            {
                conversation: 'none',    // or all
                linkColor: '#1D7EDF', // default is blue
                theme: 'light'    // or dark
            });
    });
});
