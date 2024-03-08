function Wutsi() {
    this.google_one_tap_displayed = false;

    this.is_dark_mode = function () {
        if (window.matchMedia) {
            if (window.matchMedia('(prefers-color-scheme: dark)').matches) {
                return true;
            }
        }
        return false
    }

    this.ga_track = function (category, event, value, label) {
        console.log('ga_track', category, event, value, label);

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

    this.like = function (storyId, callback) {
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
        wutsi.http_post(url)
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

                if (callback) {
                    callback(liked);
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

        let pinned = $(iconNode).hasClass('pin-icon-pinned');
        let url = pinned
            ? '/read/' + storyId + '/unpin'
            : '/read/' + storyId + '/pin';
        wutsi.http_post(url)
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

    this.http_get = function (url, json) {
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

    this.http_post = function (url, data, json) {
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
        return wutsi.http_post('/upload', form);
    };

    this.share = function (storyId) {
        const badgeNode = $('#share-badge-' + storyId);
        const title = $(badgeNode).attr('data-title');
        const url = $(badgeNode).attr('data-url');
        this.share_link(storyId, title, url);
    };

    this.share_link = function (storyId, title, url) {
        const me = this;
        if (!navigator.share || !this.isMobile()) {
            $('#share-modal .social').attr('data-story-id', storyId);
            $('#share-modal .social').attr('data-title', title);
            $('#share-modal .social').attr('data-url', url);

            $('#share-modal').modal('toggle');
        } else {
            const message = {
                title: 'Wutsi',
                text: title,
                url: url,
            };
            navigator.share(message).then(function (data) {
                console.log('share successfull', data);
                if (storyId && storyId > 0) {
                    me.http_post('/read/' + storyId + '/share');
                }
            }).catch(function (error) {
                console.error('Unable to share', error);
            })
        }
    };

    this.share_modal_callback = function (target) {
        const link = $("#share-modal a[data-target=" + target + "]");
        const title = $(link).attr('data-title');
        const url = $(link).attr('data-url');

        // Hide
        $('#share-modal').modal('hide');

        // Share
        if (target === 'facebook') {
            window.open('https://www.facebook.com/sharer/sharer.php?display=page&u=' + encodeURIComponent(url));
        } else if (target === 'linkedin') {
            window.open('https://www.linkedin.com/shareArticle?mini=true&url=' + encodeURIComponent(url) + '&title=' + encodeURIComponent(title));
        } else if (target === 'twitter') {
            window.open('https://www.twitter.com/intent/tweet?url=' + encodeURIComponent(url) + '&text=' + encodeURIComponent(title));
        } else if (target === 'reddit') {
            window.open('https://www.reddit.com/submit?url=' + encodeURIComponent(url) + '&title=' + encodeURIComponent(title));
        } else {
            return;
        }

        // Notification
        const storyId = $(link).attr('data-story-id');
        if (storyId && storyId > 0) {
            this.http_post('/read/' + storyId + '/share?target=' + target)
        }
    }

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

    this.init_ads = function () {
        let options = {
            root: null,
            threshold: 1,
        };

        const me = this;
        let observer = new IntersectionObserver(
            function (entries, observer) {
                entries.forEach(function (entry) {
                    if (entry.isIntersecting) {
                        observer.unobserve(entry.target);
                        me._show_ads(entry.target);
                    }
                });
            },
            options
        );
        document
            .querySelectorAll('.ads-banner-container')
            .forEach(function (target) {
                observer.observe(target);
            });
    }

    this._show_ads = function (target) {
        const type = target.getAttribute('wutsi-ads-type');
        const blogId = target.getAttribute('wutsi-ads-blog-id');
        let url = '/ads/banner?type=' + type;
        if (blogId) {
            url += '&blog-id=' + blogId;
        }

        wutsi.http_get(url)
            .then(function (html) {
                target.innerHTML = html;

                // Update CTA link
                const page = wutsi.page_name();
                const storyId = wutsi.story_id();
                const hitId = wutsi.hit_id();
                const adsId = target.querySelector('[data-ads-id]').getAttribute("data-ads-id");
                const link = target.querySelector('a');
                if (link) {
                    let href = link.getAttribute("href")
                        + "&hit-id=" + hitId
                        + "&page=" + page;
                    if (storyId) {
                        href += "&story-id=" + storyId;
                    }
                    if (blogId) {
                        href += "&blog-id=" + blogId;
                    }
                    link.setAttribute("href", href);
                }

                // Track impression
                wutsi.http_post( // track impression
                    '/ads/track',
                    {
                        time: new Date().getTime(),
                        event: 'impression',
                        ua: navigator.userAgent,
                        hitId: hitId,
                        url: window.location.href,
                        referrer: document.referrer,
                        campaign: adsId,
                        storyId: storyId,
                        businessId: blogId,
                        page: page,
                    },
                    true
                )
            });
    }

    this.dom_ready = function () {
        /* tracking */
        $('[wutsi-track-event]').click(function () {
            const event = $(this).attr("wutsi-track-event");
            const value = $(this).attr("wutsi-track-value");
            wutsi.ga_track(wutsi.page_name(), event, value)
        });

        /* ads */
        this.init_ads();
    };
}

var wutsi = new Wutsi();
$(document).ready(function () {
    wutsi.dom_ready()
});

// Push stores track events periodically
// setInterval(function () {
//     wutsi.track_wutsi_job()
// }, 30000);

// Handle all errors
window.onerror = function (message, source, line, col, error) {
    const label = source + ' - ' + line + ':' + col + ' ' + message;
    wutsi.ga_track('error', 'error', null, label)
};

