$(document).ready(function(){
    $('.vimeo .player').each( function() {
        const id = jQuery(this).attr('id');
        const videoId = jQuery(this).parent().attr('data-id');
        new Vimeo.Player(id, {
            id: videoId
        });
    });
});
