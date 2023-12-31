package com.wutsi.blog.app.page.admin.store

import com.wutsi.blog.app.form.StoreDiscountsForm
import com.wutsi.blog.app.page.AbstractStoreController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/me/store/discounts")
class StoreDiscountsController(
    requestContext: RequestContext,
) : AbstractStoreController(requestContext) {
    companion object {
        const val LIMIT = 20
    }

    override fun pageName() = PageName.STORE_DISCOUNTS

    @GetMapping
    fun index(model: Model): String {
        val store = checkStoreAccess()
        model.addAttribute(
            "form",
            StoreDiscountsForm(
                firstPurchaseDiscount = store.firstPurchaseDiscount,
                nextPurchaseDiscount = store.nextPurchaseDiscount,
                nextPurchaseDiscountDays = store.nextPurchaseDiscountDays,
                subscriberDiscount = store.subscriberDiscount
            )
        )
        return "admin/store/discounts"
    }

    @PostMapping
    fun submit(@ModelAttribute form: StoreDiscountsForm): String {
        val store = checkStoreAccess()
        storeService.updateDiscounts(store, form)
        return "redirect:/me/store/products"
    }
}
