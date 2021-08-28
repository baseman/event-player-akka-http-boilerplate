package co.remotectrl.shoppingcart.api

import co.remotectrl.shoppingcart.api.actors.ShoppingCartActor
import co.remotectrl.shoppingcart.api.routing.ShoppingCartRouter
import co.remotectrl.shoppingcart.api.services.ShoppingCartService
import co.remotectrl.shoppingcart.api.spring.SpringExtension
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans

class ShoppingCartInitializer : ApplicationContextInitializer<GenericApplicationContext> {

    companion object BeansInitializer{
        fun get() = beans {
            bean<SpringExtension>()
            bean<ApplicationConfig>()
            bean<ShoppingCartService>()
            bean("shoppingCartActor") {
                ShoppingCartActor(ref())
            }
            bean<ShoppingCartRouter>()
            bean<ApplicationServer>()
        }
    }

    override fun initialize(context: GenericApplicationContext) {
        get().initialize(context)
    }

}