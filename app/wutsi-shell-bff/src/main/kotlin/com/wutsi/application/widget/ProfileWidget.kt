package com.wutsi.application.widget

import com.wutsi.application.Theme
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.membership.manager.dto.Member

class ProfileWidget(
    private val displayName: String,
    private val pictureUrl: String?,
    private val active: Boolean,
    private val business: Boolean,
    private val category: String? = null,
    private val location: String? = null,
    private val biography: String? = null,
) : CompositeWidgetAware() {
    companion object {
        fun of(member: Member) = ProfileWidget(
            displayName = member.displayName,
            pictureUrl = member.pictureUrl,
            active = member.active,
            business = member.business,
            category = member.category?.title,
            location = member.city?.longName,
            biography = member.biography,
        )
    }

    override fun toWidgetAware(): WidgetAware {
        val profile = mutableListOf<WidgetAware?>(
            Text(
                caption = displayName,
                size = Theme.TEXT_SIZE_LARGE,
                color = Theme.COLOR_PRIMARY,
                bold = true,
            ),
        )

        if (!active) {
            profile.add(
                Text(
                    caption = getText("widget.profile.account-deactivated"),
                    color = Theme.COLOR_DANGER,
                ),
            )
        }

        if (active && business) {
            profile.addAll(
                listOf(
                    category?.let {
                        Text(caption = category)
                    },
                ),
            )
        }

        if (location != null) {
            profile.add(
                Row(
                    mainAxisAlignment = MainAxisAlignment.start,
                    crossAxisAlignment = CrossAxisAlignment.start,
                    children = listOf(
                        Icon(code = Theme.ICON_LOCATION, size = 14.0),
                        Container(padding = 2.0),
                        Text(location),
                    ),
                ),
            )
        }

        if (business && !biography.isNullOrEmpty()) {
            profile.addAll(
                listOf(
                    Container(padding = 5.0),
                    Text(caption = biography),
                ),
            )
        }

        return Container(
            padding = 10.0,
            child = Column(
                mainAxisAlignment = MainAxisAlignment.start,
                crossAxisAlignment = CrossAxisAlignment.start,
                children = listOf(
                    Row(
                        mainAxisAlignment = MainAxisAlignment.start,
                        crossAxisAlignment = CrossAxisAlignment.start,
                        children = listOf(
                            Flexible(
                                flex = 1,
                                child = Container(
                                    alignment = Alignment.Center,
                                    child = AvatarWidget(
                                        radius = 32.0,
                                        pictureUrl = pictureUrl,
                                        displayName = displayName,
                                    ),
                                ),
                            ),
                            Flexible(
                                flex = 3,
                                child = Column(
                                    mainAxisAlignment = MainAxisAlignment.start,
                                    crossAxisAlignment = CrossAxisAlignment.start,
                                    children = profile.filterNotNull(),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        )
    }

    private fun getText(key: String, args: Array<Any> = emptyArray()): String =
        WidgetL10n.getText(key, args)
}
