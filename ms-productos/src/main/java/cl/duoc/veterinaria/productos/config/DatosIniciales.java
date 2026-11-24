package cl.duoc.veterinaria.productos.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import cl.duoc.veterinaria.productos.modelo.Categoria;
import cl.duoc.veterinaria.productos.modelo.Producto;
import cl.duoc.veterinaria.productos.repositorio.CategoriaRepository;
import cl.duoc.veterinaria.productos.repositorio.ProductoRepository;

/** Si la base está vacía, carga las 5 categorías y los 10 productos de la tienda */
@Component
public class DatosIniciales implements CommandLineRunner {

    private final CategoriaRepository categorias;
    private final ProductoRepository productos;

    public DatosIniciales(CategoriaRepository categorias, ProductoRepository productos) {
        this.categorias = categorias;
        this.productos = productos;
    }

    @Override
    public void run(String... args) {
        if (categorias.count() > 0) {
            return;
        }
        List<Categoria> lista = categorias.saveAll(List.of(
                new Categoria("Alimentos", "Alimento seco, húmedo y snacks para perros y gatos.", "/img/alimento-perro.webp"),
                new Categoria("Juguetes", "Juguetes para entretener y estimular a tu mascota.", "/img/juguete-gato.webp"),
                new Categoria("Accesorios", "Correas, collares y accesorios para el paseo diario.", "/img/correa-perro.jpg"),
                new Categoria("Higiene", "Shampoo, arena sanitaria y productos de aseo.", "/img/placeholder-higiene.svg"),
                new Categoria("Salud", "Antiparasitarios y productos de cuidado preventivo.", "/img/placeholder-salud.svg")));
        Categoria alimentos = lista.get(0);
        Categoria juguetes = lista.get(1);
        Categoria accesorios = lista.get(2);
        Categoria higiene = lista.get(3);
        Categoria salud = lista.get(4);

        productos.saveAll(List.of(
                producto("ALI-001", "Alimento para perros", "Alimento completo para perros adultos de todas las razas. Bolsa de 15 kg.", 15990, 0, 20, 5, alimentos, "/img/alimento-perro.webp"),
                producto("JUG-001", "Juguete para gatos", "Juguete simple para entretener gatos y estimular su instinto de caza.", 2990, 20, 15, 5, juguetes, "/img/juguete-gato.webp"),
                producto("ACC-001", "Correa para perros", "Correa resistente para paseos diarios, con mango acolchado.", 7990, 0, 8, 3, accesorios, "/img/correa-perro.jpg"),
                producto("ALI-002", "Alimento para gatos", "Alimento balanceado para gatos adultos. Bolsa de 7 kg.", 12990, 0, 4, 5, alimentos, "/img/placeholder-alimentos.svg"),
                producto("JUG-002", "Pelota de goma para perros", "Pelota de goma resistente a mordidas, ideal para jugar a buscar.", 3490, 15, 25, 5, juguetes, "/img/placeholder-juguetes.svg"),
                producto("ACC-002", "Collar ajustable para gatos", "Collar ajustable con cascabel y cierre de seguridad.", 4990, 0, 3, 5, accesorios, "/img/placeholder-accesorios.svg"),
                producto("HIG-001", "Shampoo hipoalergénico", "Shampoo suave para pieles sensibles de perros y gatos. 500 ml.", 6990, 10, 12, 4, higiene, "/img/placeholder-higiene.svg"),
                producto("HIG-002", "Arena sanitaria para gatos", "Arena aglomerante con control de olores. Saco de 10 kg.", 8990, 0, 10, 4, higiene, "/img/placeholder-higiene.svg"),
                producto("SAL-001", "Pipeta antiparasitaria", "Pipeta antiparasitaria externa para perros de 10 a 20 kg.", 9990, 0, 12, 4, salud, "/img/placeholder-salud.svg"),
                producto("ALI-003", "Snack dental para perros", "Snack que ayuda a reducir el sarro. Bolsa de 7 unidades.", 4590, 25, 18, 5, alimentos, "/img/placeholder-alimentos.svg")));
    }

    private Producto producto(String codigo, String nombre, String descripcion, int precio, int descuento,
                              int stock, int stockCritico, Categoria categoria, String imagen) {
        Producto producto = new Producto();
        producto.setCodigo(codigo);
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setDescuento(descuento);
        producto.setStock(stock);
        producto.setStockCritico(stockCritico);
        producto.setCategoria(categoria);
        producto.setImagen(imagen);
        return producto;
    }
}
