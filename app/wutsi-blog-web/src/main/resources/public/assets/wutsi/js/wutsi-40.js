function Wutsi() {
    this.track = function (event, value, label, impressions) {
        // this.track_ga(wutsi.page_name(), event, value, label);
        // return this.track_wutsi(event, value, impressions);
    };

    this.track_ga = function (category, event, value, label) {
        if (typeof gtag != 'function') {
            return
        }

        try {
            gtag('event', event, {
                'event_category': category,
                'event_label': label,
                'value': (value ? value : null)
            });
        } catch (err) {
            console.error('Unable to push event to Google Analytics', err);
        }
    };

    // this.track_wutsi = function (event, value, impressions) {
    //     const page = this.page_name();
    //     const data = {
    //         time: new Date().getTime(),
    //         pid: (event == 'click' ? value : this.story_id()),
    //         event: event,
    //         page: page,
    //         ua: navigator.userAgent,
    //         value: (value ? value : null),
    //         hid: this.hit_id(),
    //         url: window.location.href,
    //         impressions: impressions
    //     };
    //     return this.httpPost('/track', data, true)
    //         .catch(function () {
    //             const key = 'track.' + data.time;
    //             const value = JSON.stringify(data);
    //             console.log('Adding into LocalStorage', key, value);
    //             localStorage.setItem(key, value);
    //         });
    // };
    //
    // this.track_wutsi_job = function () {
    //     // console.log('Running the track Job');
    //
    //     for (var i = 0; i < localStorage.length; i++) {
    //         const key = localStorage.key(i);
    //         if (key.startsWith('track.')) {
    //             try {
    //                 const data = JSON.parse(localStorage.getItem(key));
    //                 console.log('Pushing stored tracking event', key, data);
    //                 this.httpPost('/track', data, true)
    //                     .then(function () {
    //                         console.log('track-event', key, ' sent');
    //                         console.log('Removing from LocalStorage', key);
    //                         localStorage.removeItem(key);
    //                     })
    //             } catch (err) {
    //                 console.error(key, err);
    //             }
    //         }
    //     }
    // };

    this.domReady = function () {
        /* tracking */
        $('[wutsi-track-event]').click(function () {
            const event = $(this).attr("wutsi-track-event");
            const value = $(this).attr("wutsi-track-value");
            const title = $(this).attr("title");
            const impressions = $(this).attr("wutsi-track-impressions");
            wutsi.track(event, value, title, impressions);

            const rank = $(this).attr("wutsi-track-rank");
            if (rank) {
                wutsi.track_ga(wutsi.page_name(), event + '.' + rank, rank, title);
            }
        });

        /* sharing */
        $('[wutsi-share-target]').click(function () {
            const target = $(this).attr("wutsi-share-target");
            const storyId = $(this).attr("wutsi-story-id");
            if (storyId && target) {
                wutsi.share(storyId, target);
            }
        });

        /* follow */
        $('[wutsi-follow-target]').click(function () {
            const target = $(this).attr("wutsi-follow-target");
            const returnUrl = $(this).attr("wutsi-follow-return-url");
            if (target) {
                wutsi.follow(target, returnUrl);
            }
        });

    };

    this.like = function (storyId) {
        const iconNode = $('#like-badge-' + storyId + ' .like-icon');
        if ($(iconNode).attr('disabled')) {
            return;
        }

        $(iconNode).attr('disabled', 'true');
        let countNode = $('#like-badge-' + storyId + ' .like-count');
        let count = $(countNode).text();
        let liked = $(iconNode).hasClass('like-icon-liked');
        let url = liked
            ? '/read/' + storyId + '/unlike'
            : '/read/' + storyId + '/like';
        wutsi.httpGet(url)
            .then(function () {
                if (liked) {
                    $(iconNode).removeClass('like-icon-liked');
                    $(iconNode).removeClass('fas');
                    $(iconNode).addClass('far');
                    $(countNode).removeClass('like-icon-liked');
                    count = !count ? 0 : parseInt(count) - 1;
                } else {
                    $(iconNode).addClass('like-icon-liked');
                    $(iconNode).addClass('fas');
                    $(iconNode).removeClass('far');
                    $(countNode).addClass('like-icon-liked');
                    count = !count ? 1 : parseInt(count) + 1;
                }
            })
            .finally(function () {
                $(iconNode).removeAttr('disabled');
                $(countNode).text(count == 0 ? null : count);
            });
    };

    this.pin = function (storyId) {
        const iconNode = $('#pin-badge-' + storyId + ' .pin-icon');
        if ($(iconNode).attr('disabled')) {
            return;
        }

        let cardNode = $('#story-card-' + storyId);
        let pinned = $(iconNode).hasClass('pin-icon-pinned');
        let url = pinned
            ? '/read/' + storyId + '/unpin'
            : '/read/' + storyId + '/pin';
        wutsi.httpGet(url)
            .then(function () {
                $('.story-card').each(function () {
                    $(this).removeClass('story-card-pinned');
                });

                if (pinned) {
                    $(iconNode).removeClass('pin-icon-pinned');
                } else {
                    $(iconNode).addClass('pin-icon-pinned');
                    $('#story-card-' + storyId).addClass('story-card-pinned');
                }
            })
            .finally(function () {
                $(iconNode).removeAttr('disabled');
            });
    };

    this.isMobile = function () {
        const ua = navigator.userAgent;
        return /iPhone|iPad|iPod|Android/i.test(ua)
            || ((ua.indexOf("FBAN") > -1) || (ua.indexOf("FBAV") > -1))  /* Facebook in-app browser */
            ;
    };

    this.httpGet = function (url, json) {
        return new Promise(function (resolve, reject) {
            $.ajax({
                method: 'GET',
                url: url,
                dataType: json ? 'json' : null,
                contentType: json ? 'application/json' : null,
                headers: {
                    'X-CSRF-TOKEN': $("meta[name='_csrf']").attr("content")
                },
                success: function (data) {
                    console.log('GET ', url, json ? data : '');
                    resolve(data)
                },
                error: function (error) {
                    console.error('GET ', url, error);
                    reject(error)
                }
            })
        });

    };

    this.httpPost = function (url, data, json) {
        return new Promise(function (resolve, reject) {
            $.ajax({
                url: url,
                type: 'POST',
                data: json ? JSON.stringify(data) : data,
                dataType: json ? 'json' : null,
                contentType: json ? 'application/json' : false,
                cache: false,
                processData: false,
                headers: {
                    'X-CSRF-TOKEN': $("meta[name='_csrf']").attr("content")
                },
                success: function (response) {
                    console.log('POST ', url, data, response);
                    resolve(response)
                },
                error: function (error) {
                    console.error('POST ', url, data, error);
                    reject(error)
                }
            })
        });
    };

    this.upload = function (file) {
        console.log('Uploading ', file);

        const form = new FormData();
        form.append('file', file);
        return wutsi.httpPost('/upload', form);
    };

    this.share = function (storyId, target) {
        const fbAppId = document.head.querySelector("[name=facebook\\:app_id]").content;
        const title = document.head.querySelector("[property=og\\:title]").content;
        const url = document.head.querySelector("[property=og\\:url]").content;
        const xurl = url + '?utm_source=' + target;

        if (target == 'facebook') {
            window.open('https://www.facebook.com/sharer/sharer.php?display=page&u=' + encodeURIComponent(xurl));
        } else if (target == 'linkedin') {
            window.open('https://www.linkedin.com/shareArticle?mini=true&url=' + encodeURIComponent(xurl) + '&title=' + encodeURIComponent(title));
        } else if (target == 'twitter') {
            window.open('http://www.twitter.com/intent/tweet?url=' + encodeURIComponent(xurl) + '&text=' + encodeURIComponent(title));
        } else if (target == 'whatsapp') {
            window.location.href = 'whatsapp://send?text=' + encodeURIComponent(xurl);
        } else if (target == 'messenger') {
            window.location.href = 'fb-messenger://share/?app_id=' + fbAppId + '&link=' + encodeURIComponent(xurl);
        } else if (target == 'telegram') {
            window.location.href = 'https://telegram.me/share/url?url=' + xurl + '&text=' + encodeURIComponent(title);
        }

        this.track('share-' + target);
    };

    this.follow = function (id, returnUrl) {
        const url = '/follow?userId=' + id + '&return=' + encodeURIComponent(returnUrl);
        document.location.href = url;
    };

    this.linkify = function (selector) {
        $(selector).each(function () {
            const text = $(this).html();
            const xtext = text.replace(/((http|https|ftp):\/\/[\w?=&.\/-;#~%-]+(?![\w\s?&.\/;#~%"=-]*>))/g, '<a target="_new" href="$1">$1</a> ');
            $(this).html(xtext);
        });
    };

    this.cookie = function (name) {
        var match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
        if (match) return match[2];
    };

    this.page_name = function () {
        var meta = document.head.querySelector("[name=wutsi\\:page_name]");
        return meta ? meta.content : null
    };

    this.story_id = function () {
        const meta = document.head.querySelector("[name=wutsi\\:story_id]");
        return meta ? meta.content : null
    };

    this.hit_id = function () {
        var meta = document.head.querySelector("[name=wutsi\\:hit_id]");
        return meta ? meta.content : null
    };
}

var wutsi = new Wutsi();

// Push stores track events periodically
// setInterval(function () {
//     wutsi.track_wutsi_job()
// }, 30000);

// Handle all errors
window.onerror = function (message, source, line, col, error) {
    const label = source + ' - ' + line + ':' + col + ' ' + message;
    wutsi.track_ga('error', 'error', null, label)
};

$(document).ready(function () {
    wutsi.domReady();
});

