package bj.restaurant.couts.dao;

import bj.restaurant.couts.Entities.ParametresRestaurant;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;

@ApplicationScoped
public class ParametresRestaurantDAO {
	@PersistenceContext(unitName = "BrishPU")
	private EntityManager entityManager;

	public Optional<ParametresRestaurant> trouver() {
		return Optional.ofNullable(entityManager.find(ParametresRestaurant.class, 1));
	}
}
