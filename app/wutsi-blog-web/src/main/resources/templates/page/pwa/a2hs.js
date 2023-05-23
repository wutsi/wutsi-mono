var deferredPrompt;

self.addEventListener('beforeinstallprompt', function (e) {
    console.log('beforeinstallprompt');

    // Prevent the mini-infobar from appearing on mobile
    e.preventDefault();

    // Stash the event so it can be triggered later.
    deferredPrompt = e;

    // Show UI notify the user they can install the PWA
    console.log('Showing PWA App Installer');
    $('#a2hs-container').show(0, function(){
        console.log('PWA App Installer is visible');
        wutsi.track('pwa_a2hs_show');
    });

    $('#a2hs-container .btn-install').click(function () {
        // Hide the app provided install promotion
        $('#a2hs-container').hide();

        // Show the install prompt
        deferredPrompt.prompt();

        // Wait for the user to respond to the prompt
        deferredPrompt.userChoice.then(function (choiceResult) {
            if (choiceResult.outcome === 'accepted') {
                console.log('User accepted the install prompt');
            } else {
                console.log('User dismissed the install prompt');
            }
            deferredPrompt = null;
        });
    });
});


self.addEventListener('appinstalled', function () {
    console.log('PWA App install');
    wutsi.track('pwa_a2hs_install');
});
