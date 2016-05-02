package wdsr.exercise3.client;

import java.util.List;
import java.util.Set;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.glass.ui.Application;
import wdsr.exercise3.model.Product;
import wdsr.exercise3.model.ProductType;

public class ProductService extends RestClientBase {
	protected ProductService(final String serverHost, final int serverPort, final Client client) {
		super(serverHost, serverPort, client);
	}

	@Context
	Application application;
	private final WebTarget statusTarget = baseTarget.path("/products");

	/**
	 * Looks up all products of given types known to the server.
	 * 
	 * @param types
	 *            Set of types to be looked up
	 * @return A list of found products - possibly empty, never null.
	 */
	public List<Product> retrieveProducts(Set<ProductType> types) {
		return statusTarget.queryParam("type", types.toArray()).request().get(new GenericType<List<Product>>() {
		});
	}

	/**
	 * Looks up all products known to the server.
	 * 
	 * @return A list of all products - possibly empty, never null.
	 */
	public List<Product> retrieveAllProducts() {
		return statusTarget.request().get(new GenericType<List<Product>>() {
		});
	}

	/**
	 * Looks up the product for given ID on the server.
	 * 
	 * @param id
	 *            Product ID assigned by the server
	 * @return Product if found
	 * @throws NotFoundException
	 *             if no product found for the given ID.
	 */
	public Product retrieveProduct(int id) {
		return statusTarget.path(String.valueOf(id)).request().get(new GenericType<Product>() {
		});
	}

	/**
	 * Creates a new product on the server.
	 * 
	 * @param product
	 *            Product to be created. Must have null ID field.
	 * @return ID of the new product.
	 * @throws WebApplicationException
	 *             if request to the server failed
	 */
	public int storeNewProduct(Product product) {
		Response response = statusTarget.request().post(Entity.entity(product, MediaType.APPLICATION_JSON),
				Response.class);
		if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
			throw new WebApplicationException();
		}
		response.close();
		return Integer.parseInt(
				response.getLocation().toString().substring(response.getLocation().toString().lastIndexOf("/") + 1));
	}

	/**
	 * Updates the given product.
	 * 
	 * @param product
	 *            Product with updated values. Its ID must identify an existing
	 *            resource.
	 * @throws NotFoundException
	 *             if no product found for the given ID.
	 */
	public void updateProduct(Product product) {
		if (retrieveProduct(product.getId()) != null) {
			statusTarget.path(String.valueOf(product.getId())).request()
					.put(Entity.entity(product, MediaType.APPLICATION_JSON));
			;
		} else {
			throw new NotFoundException();
		}

	}

	/**
	 * Deletes the given product.
	 * 
	 * @param product
	 *            Product to be deleted. Its ID must identify an existing
	 *            resource.
	 * @throws NotFoundException
	 *             if no product found for the given ID.
	 */
	public void deleteProduct(Product product) {
		if (retrieveProduct(product.getId()) != null) {
			statusTarget.path(String.valueOf(product.getId())).request().delete();
		} else {
			throw new NotFoundException();
		}

	}
}
