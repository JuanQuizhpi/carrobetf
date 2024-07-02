package ec.edu.ups.pweb.dao;

import java.util.List;

import ec.edu.ups.pweb.model.Carro;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Stateless
public class CarroDAO{
	
	
	@PersistenceContext
	private EntityManager em;
	
	public void insert(Carro carro) {
		em.persist(carro);
	}
	
	public void update(Carro carro){
		em.merge(carro);
	}
	public Carro read(String placa) {
		Carro p = em.find(Carro.class, placa);
		return p;
	}
	
	public void delete(String placa) {
		Carro p = em.find(Carro.class, placa);
		em.remove(p);
	}
	
	public List<Carro> getList(){
		String jsql = "SELECT p FROM Carro p";
		Query query = em.createQuery(jsql,Carro.class);
		List<Carro> lista = query.getResultList();
		return lista;
	}
	
	public Carro getCarroPorPlaca(String placa){
		String jpql = "SELECT c FROM Carro c WHERE c.placa = :placa";
		Query q = em.createQuery(jpql, Carro.class);
		q.setParameter("placa", placa);
		List<Carro> carros = q.getResultList();
		if(carros.size()>0)
			return carros.get(0);
		return null;
	}
	
	
	
}