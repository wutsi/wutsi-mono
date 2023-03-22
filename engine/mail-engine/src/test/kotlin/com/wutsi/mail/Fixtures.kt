package com.wutsi.mail

object Fixtures {
    fun createMailContext() = MailContext(
        assetUrl = "https://s3.amazonaws.com/int-wutsi",
        merchant = Merchant(
            logoUrl = "https://ik.imagekit.io/cx8qxsgz4d/user/12/picture/tr:w-64,h-64,fo-face/023bb5c8-7b09-4f2f-be51-29f5c851c2c0-scaled_image_picker1721723356188894418.png",
            name = "Maison H",
            url = "https://www.com/u/1",
            category = "Bakery",
            location = "Yaounde, Cameroon",
            phoneNumber = "+23767000000",
            websiteUrl = "http://www.goo.com",
            facebookId = "11111",
            youtubeId = "11111",
            instagramId = "11111",
            twitterId = "1111",
            whatsapp = true,
            country = "CM",
        ),
    )
}
