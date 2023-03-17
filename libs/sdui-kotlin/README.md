[![](https://github.com/wutsi/wutsi-mono/actions/workflows/libs-sdui-kotlin-master.yml/badge.svg)](https://github.com/wutsi/wutsi-mono/actions/workflows/libs-sdui-kotlin-master.yml)

![](https://img.shields.io/github/v/tag/wutsi/sdui-kotlin)
![](https://img.shields.io/badge/licence-MIT-yellow.svg)
![](https://img.shields.io/badge/language-kotlin-blue.svg)
![](https://img.shields.io/badge/language-flutter-darkblue.svg)

# SDUI-Kotlin

`sdui-kotlin` make it easy to implement Server Driven UI pattern in Kotlin, for [Flutter](https://flutter.dev/) using
the [SDUI](https://pub.dev/packages/sdui) library.

It provides a list of classes that represent various Flutter widgets.

- You'll use this library to describe on the server the UI widget hierarchy
- The UI widget hierarchy will be serialized to JSON and returned to the client Flutter application.
- The client Flutter application will use [SDUI](https://pub.dev/packages/sdui) to automatically render the UI

## Example

### The Server

```kotlin
Here is an example of server code based on Spring

@RestController
@RequestMapping("/app/onboard/screens/home")
class HomeScreen(
    private val urlBuilder: URLBuilder
) : AbstractQuery() {
    @PostMapping
    fun index() = Screen(
        safe = true,
        child = Container(
            alignment = Center,
            padding = 10.0,
            child = Column(
                children = listOf(
                    Spacer(
                        flex = 1
                    ),
                    Flexible(
                        flex = 1,
                        child = Container(
                            alignment = BottomCenter,
                            child = Text(
                                caption = "Enter your phone number",
                                alignment = TextAlignment.Center,
                                size = 24.0,
                                bold = true
                            )
                        )
                    ),
                    Flexible(
                        flex = 1,
                        child = Container(
                            alignment = TopCenter,
                            child = Text(
                                caption = "Wutsi will send a SMS to verify your phone number",
                                alignment = TextAlignment.Center,
                                size = 16.0,
                            )
                        )
                    ),
                    Spacer(
                        flex = 1
                    ),
                    Flexible(
                        flex = 8,
                        child = Form(
                            padding = 10.0,
                            children = listOf(
                                Input(
                                    name = "phoneNumber",
                                    type = Phone,
                                    caption = "Phone Number",
                                    required = true
                                ),
                                Input(
                                    name = "command",
                                    type = Submit,
                                    caption = "Next",
                                    action = Action(
                                        type = Command,
                                        url = "https://www.foo.com/app/onboard/commands/send-sms-code")
                                )
                            )
                        )
                    )
                )
            )
        )
    ).toWidget()
}
```

## The Json

Here is the equivalent JSON returned to the Flutter app

```json
{
    "type": "Screen",
    "attributes": {
        "safe": true
    },
    "children": [
        {
            "type": "Container",
            "attributes": {
                "padding": "10"
            },
            "children": [
                {
                    "type": "Column",
                    "children": [
                        {
                            "type": "Spacer",
                            "attributes": {
                                "flex": 1
                            }
                        },
                        {
                            "type": "Flexible",
                            "attributes": {
                                "flex": 1
                            },
                            "children": [
                                {
                                    "type": "Container",
                                    "attributes": {
                                        "alignment": "BottomCenter"
                                    },
                                    "children": [
                                        {
                                            "type": "Text",
                                            "attributes": {
                                                "caption": "Enter your phone number",
                                                "alignment": "Center",
                                                "size": 24
                                            }
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            "type": "Flexible",
                            "attributes": {
                                "flex": 1
                            },
                            "children": [
                                {
                                    "type": "Container",
                                    "attributes": {
                                        "alignment": "TopCenter"
                                    },
                                    "children": [
                                        {
                                            "type": "Text",
                                            "attributes": {
                                                "caption": "Wutsi will send a SMS to verify your phone number",
                                                "alignment": "Center",
                                                "size": 16
                                            }
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            "type": "Spacer",
                            "attributes": {
                                "flex": 1
                            }
                        },
                        {
                            "type": "Flexible",
                            "attributes": {
                                "flex": 8
                            },
                            "children": [
                                {
                                    "type": "Form",
                                    "attributes": {
                                        "padding": 10
                                    },
                                    "children": [
                                        {
                                            "type": "Input",
                                            "attributes": {
                                                "name": "phoneNumber",
                                                "caption": "Phone Number",
                                                "type": "Phone",
                                                "required": true
                                            }
                                        },
                                        {
                                            "type": "Input",
                                            "attributes": {
                                                "name": "command",
                                                "caption": "Next",
                                                "type": "Submit",
                                                "action": {
                                                    "type": "Command",
                                                    "url": "https://www.foo.com/app/onboard/commands/send-sms-code"
                                                }
                                            }
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    ]
}
```

## The UI

![](doc/images/screenshot.png)
