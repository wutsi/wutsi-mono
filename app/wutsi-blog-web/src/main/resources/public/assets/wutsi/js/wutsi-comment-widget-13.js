function WutsiCommentWidget(storyId, anonymous, storyUrl) {
    this.visible = false;
    this.config = {
        selectors: {
            text: '.comment-text',
            content: '.comment-widget-content',
            list: '.comment-widget-list'
        }
    };

    this.load = function () {
        console.log('Loading comments', storyId);

        const me = this;
        $(document).mouseup(function (e) {
            if (!me.visible) {
                return;
            }

            var container = $(me.selector(''));
            if (!container.is(e.target) && container.has(e.target).length === 0) {
                me.hide();
            }
        });
    };

    this.show = function () {
        console.log('Showing comments...');

        const selector = this.selector(this.config.selectors.content);
        $(selector).show();
        this.visible = true;

        wutsi.httpGet('/comment/widget?storyId=' + storyId, false)
            .then(function (html) {
                $(selector).html(html);
            });
    };

    this.hide = function () {
        console.log('Hiding comments...');

        const selector = this.selector(this.config.selectors.content);
        $(selector).hide();
        this.visible = false;
        wutsi.update_comment_count();
    };

    this.load_items = function () {
        const selector = this.selector(this.config.selectors.list);
        wutsi.httpGet('/comment/list?storyId=' + storyId, false)
            .then(function (html) {
                $(selector).html(html);

                // Linkify the text
                wutsi.linkify(selector + ' .comment .text');
            });
    };


    this.contentReady = function () {
        const me = this;

        this.end_edit();
        this.load_items();

        const textarea = this.selector('textarea');
        $(textarea).click(function () {
            if ($(this).is('[readonly]')) {
                me.begin_edit();
            }
        });
        if (!anonymous) {
            me.begin_edit();
        }

        const button = this.selector('.btn-submit');
        $(button).click(function () {
            me.submit($(textarea).val());
        });

        const close = this.selector('.close');
        $(close).click(function () {
            me.hide();
        });
    };

    this.begin_edit = function () {
        console.log('Begin comment edition');

        if (anonymous) {
            const redirect = storyUrl + '?comment=1';
            window.location.href = '/login?reason=comment&redirect=' + encodeURI(redirect) + '&return=' + encodeURI(redirect);
        } else {
            const textarea = this.selector('textarea');
            $(textarea).removeAttr('readonly');
            $(textarea).attr('rows', '3');
            $(textarea).val('');
            $(textarea).focus();

            const button = this.selector('.btn-submit');
            $(button).show();
        }
    };

    this.end_edit = function () {
        console.log('Stop comment edition');

        const textarea = this.selector('textarea');
        $(textarea).attr('readonly', 'readonly');
        $(textarea).attr('rows', '1');
        $(textarea).val('');

        const button = this.selector('.btn-submit');
        $(button).hide();
    };

    this.submit = function (text) {
        console.log('Submitting comment', text);
        const data = {
            storyId: storyId,
            text: text
        };
        const me = this;
        wutsi.httpPost('/comment', data, true)
            .then(function () {
                me.load_items();
                wutsi.track('comment')
            })
            .catch(function (error) {
                console.log('Unable to create comment', error);
            })
            .finally(function () {
                me.end_edit();
            });
    };

    this.selector = function (q) {
        return '#comment-widget-' + storyId + ' ' + q;
    }
}
