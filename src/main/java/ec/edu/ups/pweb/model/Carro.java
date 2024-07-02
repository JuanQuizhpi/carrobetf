package ec.edu.ups.pweb.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Carro implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	
	@Id
	@Column(name="car_placa")
	private String placa;
	@Column(name="car_modelo")
	private String modelo;
	@Column(name="car_marca")
	private String marca;
	
	
	public String getPlaca() {
		return placa;
	}
	public void setPlaca(String placa) {
		this.placa = placa;
	}
	public String getModelo() {
		return modelo;
	}
	public void setModelo(String modelo) {
		this.modelo = modelo;
	}
	public String getMarca() {
		return marca;
	}
	public void setMarca(String marca) {
		this.marca = marca;
	}
	@Override
	public String toString() {
		return "Carro [placa=" + placa + ", modelo=" + modelo + ", marca=" + marca + "]";
	}
	
}
