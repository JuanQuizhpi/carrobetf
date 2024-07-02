package ec.edu.ups.pweb.business;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.List;

import ec.edu.ups.pweb.dao.CarroDAO;
import ec.edu.ups.pweb.model.Carro;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

@Stateless
public class GestionCarros {

	@Inject
	private CarroDAO daoCarro;

	
	public void guardarCarros(Carro carro) {
		// TODO Auto-generated method stub
		Carro car = daoCarro.read(carro.getPlaca());
		if (car != null){
			daoCarro.update(carro);
		}else {
			daoCarro.insert(carro);
		}
		this.enviarCorreo("Rigistro de Carro", "Se registra un carro con placa: " + carro.getPlaca()+ " Marca: "+ carro.getMarca() + " Modelo: " + carro.getModelo());
	}

	
	public void actualizarCarro(Carro carro) throws Exception {
		// TODO Auto-generated method stub
		Carro car = daoCarro.read(carro.getPlaca());
		if (car != null){
			daoCarro.update(carro);
		}else {
			throw new Exception("Carro no existe");
		}
	}

	
	public Carro getCarroPorPlaca(String placa) throws Exception {
		// TODO Auto-generated method stub
		Carro car = daoCarro.read(placa);
		if(car != null) {
			return daoCarro.getCarroPorPlaca(placa);
		}else {
			throw new Exception("Carro no existe");
		}
	}

	
	public void borrarCarro(String placa) {
		// TODO Auto-generated method stub
		daoCarro.delete(placa);
	}

	
	public List<Carro> getCarros() {
		// TODO Auto-generated method stub
		return daoCarro.getList();
	}
	
	private void enviarCorreo(String subject, String body) {
        String apiEndpoint = "http://localhost:8080/correojq/rs/sendEmail";

        String mensaje = "{\"subject\": \"" + subject + "\", \"body\": \"" + body + "\"}";

        HttpClient httpClient = HttpClient.newHttpClient();
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiEndpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mensaje))
                .build();

        
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            String responseBody = response.body();

            System.out.println("Status code: " + statusCode);
            System.out.println("Response body: " + responseBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
	}
	
}