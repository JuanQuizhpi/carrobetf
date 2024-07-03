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

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

@Stateless
public class GestionCarros {

	@Inject
	private CarroDAO daoCarro;
	
	//Definir las propiedades del circuit breaker
	private int failures = 0;
    private int maxFailures = 3;  // Número máximo de fallos antes de abrir el Circuit Breaker
    private Duration timeout = Duration.ofMinutes(1);  // Tiempo de espera antes de intentar nuevamente
    private Instant lastFailureTime = Instant.MIN;
    private State state = State.CLOSED;

 // Lista de endpoints de servidores de correo
    private List<String> apiEndpoints = new ArrayList<>();
    private int currentEndpointIndex = 0;

    public GestionCarros() {
        // Inicializar los endpoints de servidores de correo
        apiEndpoints.add("http://34.23.111.107:8080/correojq/rs/sendEmail");
        apiEndpoints.add("http://104.198.200.30:8080/correojq/rs/sendEmail");
    }
    
    

	
	public void guardarCarros(Carro carro) {
		// TODO Auto-generated method stub
		Carro car = daoCarro.read(carro.getPlaca());
		if (car != null){
			daoCarro.update(carro);
		}else {
			daoCarro.insert(carro);
		}
		//this.enviarCorreo("Rigistro de Carro", "Se registra un carro con placa: " + carro.getPlaca()+ " Marca: "+ carro.getMarca() + " Modelo: " + carro.getModelo());
		try {
            enviarCorreo("Registro de Carro", "Se registra un carro con placa: " + carro.getPlaca() + " Marca: " + carro.getMarca() + " Modelo: " + carro.getModelo());
        } catch (Exception e) {
            System.out.println("Error al enviar correo: " + e.getMessage());
            // Manejar la excepción y activar el Circuit Breaker
            handleFailure();
        }
	}
	
	// Método para manejar fallos y activar el Circuit Breaker
    private void handleFailure() {
        failures++;
        if (failures >= maxFailures) {
            open();
        }
    }

    // Método para abrir el Circuit Breaker
    private void open() {
        state = State.OPEN;
        lastFailureTime = Instant.now();
    }

    // Método para intentar nuevamente después de un tiempo de espera
    private void halfOpen() {
        state = State.HALF_OPEN;
        failures = 0;
    }

    // Método para restablecer el Circuit Breaker
    private void reset() {
        state = State.CLOSED;
        failures = 0;
    }

    // Enum para los estados del Circuit Breaker
    private enum State {
        CLOSED,
        OPEN,
        HALF_OPEN
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
	
	// Método de envío de correo con integración del Circuit Breaker
    private void enviarCorreo(String subject, String body) throws Exception {
        String currentEndpoint = apiEndpoints.get(currentEndpointIndex);

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(currentEndpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"subject\": \"" + subject + "\", \"body\": \"" + body + "\"}"))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            String responseBody = response.body();

            System.out.println("Status code: " + statusCode);
            System.out.println("Response body: " + responseBody);

            // Reiniciar el estado del Circuit Breaker si la solicitud fue exitosa
            reset();
        } catch (Exception e) {
            // Si ocurre un error, manejarlo y activar el Circuit Breaker
            handleFailure();
            throw e;
        } finally {
            // Avanzar al siguiente endpoint para el próximo intento (round-robin)
            currentEndpointIndex = (currentEndpointIndex + 1) % apiEndpoints.size();
        }
    }
	
}