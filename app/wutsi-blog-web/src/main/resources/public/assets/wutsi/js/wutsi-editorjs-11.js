function WutsiEJS(holderId, publishCallback) {
    this.holderId = holderId;
    this.editorjs = null;
    this.model = {
        id: 0,
        title: '',
        content: {},
        draft: true,
        dirty: false
    };

    this.config = {
        autosave: 15000,
        saveUrl: '/editor/save',
        selectors: {
            title: '#title',
            btnPublish: '#btn-publish',
            btnClose: '#btn-close',
            storyStatusDraft: '#story-status-draft',
            storyStatusPublished: '#story-status-published',
            saveStatus: '#save-status'
        }
    };

    this.setup = function (storyId) {
        console.log('Setup story', storyId);

        /* load locally */
        const data = window.localStorage.getItem("document-" + storyId);
        if (data) {
            console.log('Initializing from local storage');
            this.init(JSON.parse(data));
        } else {

            /* load from server */
            if (storyId == 0) {
                console.log('Initializing with empty');
                this.init({
                    id: 0,
                    title: '',
                    content: {},
                    draft: true
                });
            } else {
                const me = this;
                wutsi.httpGet('/editor/fetch/' + storyId, true)
                    .then(function (story) {
                        const model = {
                            id: story.id,
                            title: story.title,
                            content: story.content && story.content.length > 0 ? JSON.parse(story.content) : {},
                            draft: story.draft
                        };
                        console.log('Initializing from server');
                        me.init(model);
                    })
                    .catch(function (error) {
                        console.log('Unexpected error when loading the story', error);

                        var selector = '';
                        if (error.status == 404) {
                            selector = '#story-load-error .not-found';
                        } else if (error.status == 403) {
                            selector = '#story-load-error .permission-denied';
                        } else {
                            selector = '#story-load-error .unknown';
                        }

                        $(selector).removeClass('hidden');
                        $('#story-load-error').removeClass('hidden');
                        $('#story-editor').addClass('hidden');
                    });

            }
        }
    };

    this.init = function (model) {
        console.log('Initializing', model);

        this.model = model;
        this.init_title(model);
        this.init_editorjs(model);
        this.init_toolbar();
        this.init_autosave();

        $(this.config.selectors.title).keypress(function () {
            model.dirty = true;
        });
    };

    this.init_title = function (model) {
        console.log('Initializing title');

        $(this.config.selectors.title).text(model.title);
    };

    this.init_editorjs = function (model) {
        console.log('Initializing EditorJS');

        const tools = {
            header: {
                class: Header,
                config: {
                    levels: [2, 3, 4],
                    defaultLevel: 2
                }
            },
            quote: Quote,
            delimiter: Delimiter,

            list: {
                class: List,
                inlineToolbar: true
            },
            marker: Marker,

            linkTool: {
                class: LinkTool,
                config: {
                    endpoint: '/ejs/link/fetch'
                }
            },

            embed: {
                class: Embed,
                config: {
                    inlineToolbar: true,
                    services: {
                        youtube: true,
                        twitter: true,
                        vimeo: true,
                        instagram: false
                    }
                }
            },

            image: {
                class: ImageTool,
                config: {
                    uploader: {
                        uploadByFile: function (file) {
                            return wutsi.upload(file)
                                .then(function (data) {
                                    return {
                                        success: 1,
                                        file: {
                                            url: data.url,
                                            width: data.width,
                                            height: data.height
                                        }
                                    }
                                })
                        },

                        uploadByUrl: function (url) {
                            return wutsi.httpGet('/upload?url=' + url)
                                .then(function (data) {
                                    return {
                                        success: 1,
                                        file: {
                                            url: data.url,
                                            width: data.width,
                                            height: data.height
                                        }
                                    }
                                })
                        }
                    }
                }
            },

            inlineCode: InlineCode,
            code: CodeTool,
            raw: RawTool,

            button: {
                class: Button,
                config: {
                    label: "Button",
                    target: "_blank",
                    classes: ["button", "ejs-btn"]
                }
            }
        };

        this.editorjs = new EditorJS({
            holderId: this.holderId,
            autofocus: true,
            tools: tools,
            data: model.content,
            onChange: function () {
                model.dirty = true;
            }
        });
    };

    this.init_toolbar = function () {
        const me = this;

        // Publish button
        if (this.model.draft) {
            $(this.config.selectors.storyStatusDraft).removeClass('hidden');
        } else {
            $(this.config.selectors.storyStatusPublished).removeClass('hidden');
        }
        $(this.config.selectors.btnPublish).on('click', function () {
            me.editorjs_server_save(function (story) {
                publishCallback(story.id);
            })
        });

        // Close button
        $(this.config.selectors.btnClose).on('click', function () {
            me.editorjs_server_save(function () {
                if (me.model.draft) {
                    window.location.href = '/me/draft';
                } else {
                    window.location.href = '/me/story/published';
                }
            });
        });
    };

    this.init_autosave = function () {
        const me = this;
        setInterval(function () {
            me.editorjs_local_save()
        }, this.config.autosave);
    };


    this.editorjs_server_save = function (resolve) {
        console.log('Saving remotely');

        const me = this;
        const storyId = this.model.id;
        const title = $(this.config.selectors.title).val();
        const saveUrl = this.config.saveUrl;

        this.saving();
        return this.editorjs
            .save()
            .then(function (data) {
                const request = {
                    id: storyId,
                    title: title,
                    content: JSON.stringify(data)
                };

                wutsi.httpPost(saveUrl, request, true)
                    .then(function (story) {
                        window.localStorage.removeItem("document-" + storyId);

                        me.storyId = story.id;
                        me.saved();

                        if (resolve) {
                            resolve(story);
                        }
                    });
            });
    };

    this.editorjs_local_save = function () {
        console.log('Saving locally. dirty=' + this.model.dirty);
        if (this.model.dirty == false) {
            return;
        }

        this.saving();

        this.model.title = $(this.config.selectors.title).text();

        const id = this.model.id;
        const me = this;
        this.editorjs
            .save()
            .then(function (data) {
                console.log('Saved', data);

                me.model.content = data;
                window.localStorage.setItem('document-' + id, JSON.stringify(me.model));
                me.saved();
            })
            .catch(function (error) {
                me.saved(error);
            });
    };

    this.saving = function () {
        $(this.config.selectors.saveStatus).removeClass('hidden');
    };

    this.saved = function (error) {
        $(this.config.selectors.saveStatus).addClass('hidden');
        this.model.dirty = (error != null);
    };
}
