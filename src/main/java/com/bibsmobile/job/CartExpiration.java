package com.bibsmobile.job;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.quartz.JobExecutionContext;
import org.springframework.transaction.annotation.Transactional;

import com.bibsmobile.model.Cart;
import com.bibsmobile.model.CartItem;
import com.bibsmobile.model.EventCartItem;

public class CartExpiration extends BaseJob {
    @Override
    @Transactional
    public void execute(JobExecutionContext context) {
        EntityManager em = Cart.entityManager();
        TypedQuery<Cart> q = em.createQuery("SELECT c FROM Cart c WHERE c.status = :statusNew AND c.timeout > 0 AND UNIX_TIMESTAMP(c.created) + c.timeout < UNIX_TIMESTAMP()",
                Cart.class);
        q.setParameter("statusNew", Cart.NEW);
        for (Cart c : q.getResultList()) {
            expireCart(c);
        }
    }

    @Transactional
    private void expireCart(Cart c) {
        // TODO should be a transaction
        for (CartItem ci : c.getCartItems()) {
            EventCartItem eci = ci.getEventCartItem();
            eci.setAvailable(eci.getAvailable() + ci.getQuantity());
            eci.persist();
        }
        EntityManager em = Cart.entityManager();
        Query q = em.createQuery("DELETE FROM CartItem ci WHERE ci.cart = :cart");
        q.setParameter("cart", c);
        q.executeUpdate();
        q = em.createQuery("DELETE FROM Cart c WHERE c.id = :id");
        q.setParameter("id", c.getId());
        q.executeUpdate();
    }
}
