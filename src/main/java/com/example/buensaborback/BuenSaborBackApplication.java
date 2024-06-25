package com.example.buensaborback;

import com.example.buensaborback.domain.entities.*;
import com.example.buensaborback.domain.enums.*;
import com.example.buensaborback.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalTime;

@SpringBootApplication
public class BuenSaborBackApplication {
// Aca tiene que inyectar todos los repositorios
// Es por ello que deben crear el paquete reositorio

// Ejemplo  @Autowired
//	private ClienteRepository clienteRepository;

    private static final Logger logger = LoggerFactory.getLogger(BuenSaborBackApplication.class);

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PaisRepository paisRepository;

    @Autowired
    private ProvinciaRepository provinciaRepository;

    @Autowired
    private LocalidadRepository localidadRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private SucursalRepository sucursalRepository;

    @Autowired
    private UnidadMedidaRepository unidadMedidaRepository;

    @Autowired
    private ArticuloInsumoRepository articuloInsumoRepository;

    @Autowired
    private ArticuloManufacturadoRepository articuloManufacturadoRepository;

    @Autowired
    private PromocionRepository promocionRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    public static void main(String[] args) {
        SpringApplication.run(BuenSaborBackApplication.class, args);
        logger.info("Me ejecutaste");
        System.out.println("jdbc:h2:mem:testdb");
        System.out.println("http://localhost:8080/h2-console/");
        System.out.println("SELECT * FROM UNIDAD_MEDIDA ;\n" +
                "SELECT * FROM PAIS ;\n" +
                "SELECT * FROM PROVINCIA ;\n" +
                "SELECT * FROM LOCALIDAD ;\n" +
                "SELECT * FROM DOMICILIO ;\n" +
                "SELECT * FROM EMPRESA ;\n" +
                "SELECT * FROM USUARIO_EMPLEADO ;\n" +
                "SELECT * FROM EMPLEADO ;\n" +
                "SELECT * FROM IMAGEN_EMPLEADO ;\n" +
                "SELECT * FROM SUCURSAL ;\n" +
                "SELECT * FROM PROMOCION_SUCURSAL;\n" +
                "SELECT * FROM SUCURSAL_CATEGORIA ;\n" +
                "SELECT * FROM CATEGORIA ;\n" +
                "SELECT * FROM IMAGEN_ARTICULO ;\n" +
                "SELECT * FROM ARTICULO_INSUMO;\n" +
                "SELECT * FROM ARTICULO_MANUFACTURADO ;\n" +
                "SELECT * FROM ARTICULO_MANUFACTURADO_DETALLE;\n" +
                "SELECT * FROM STOCK_INSUMO;\n" +
                "SELECT * FROM IMAGEN_PROMOCION ;\n" +
                "SELECT * FROM PROMOCION ;\n" +
                "SELECT * FROM PROMOCION_DETALLE ;\n" +
                "SELECT * FROM USUARIO_CLIENTE ;\n" +
                "SELECT * FROM CLIENTE;\n" +
                "SELECT * FROM IMAGEN_CLIENTE ;\n" +
                "SELECT * FROM CLIENTE_DOMICILIO ;\n" +
                "SELECT * FROM PEDIDO ;\n" +
                "SELECT * FROM DETALLE_PEDIDO ;\n" +
                "SELECT * FROM FACTURA ;");
        System.out.println("http://localhost:8080/swagger-ui/index.html");
    }

    @Bean
    CommandLineRunner init() {
        return args -> {
            logger.info("----------------ESTOY----FUNCIONANDO---------------------");
            RestTemplate restTemplate = new RestTemplate();
            String jsonResponse = restTemplate.getForObject("https://infra.datos.gob.ar/georef/departamentos.json", String.class);
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray departamentosArray = jsonObject.getJSONArray("departamentos");

            // Etapa del dashboard
            // Crear 1 pais
            // Crear 2 provincias para ese pais
            // crear 2 localidades para cada provincia
            Pais pais1 = Pais.builder().nombre("Argentina").build();
            logger.info("Pais {}", pais1);
            Provincia provincia1 = Provincia.builder().nombre("Mendoza").pais(pais1).build();
            Provincia provincia2 = Provincia.builder().nombre("Cordoba").pais(pais1).build();

            // Creación de provincias
            logger.info("Provincia {}", provincia1);
            logger.info("Provincia {}", provincia2);
            Localidad localidad1 = Localidad.builder().nombre("Lujan de Cuyo").provincia(provincia1).build();
            Localidad localidad2 = Localidad.builder().nombre("Godoy Cruz").provincia(provincia1).build();
            Localidad localidad3 = Localidad.builder().nombre("Achiras").provincia(provincia2).build();
            Localidad localidad4 = Localidad.builder().nombre("Agua de Oro").provincia(provincia2).build();
            localidadRepository.saveAll(Set.of(localidad1, localidad2, localidad3, localidad4));

            Pais pais = paisRepository.findById(1L).orElseGet(() -> {
                Pais newPais = new Pais();
                newPais.setId(1L);
                newPais.setNombre("Argentina");
                return paisRepository.save(newPais);
            });

            departamentosArray.forEach(obj -> {
                JSONObject departamentoJson = (JSONObject) obj;

                Long localidadId = Long.parseLong(departamentoJson.getString("id"));
                String localidadNombre = departamentoJson.getString("nombre");

                JSONObject provinciaJson = departamentoJson.getJSONObject("provincia");
                Long provinciaId = Long.parseLong(provinciaJson.getString("id"));
                String provinciaNombre = provinciaJson.getString("nombre");

                // Verificar si la provincia ya existe por nombre, si no, crearla y guardarla
                Provincia provincia = provinciaRepository.findByNombre(provinciaNombre);
                if (provincia == null) {
                    provincia = new Provincia();
                    provincia.setId(provinciaId);
                    provincia.setNombre(provinciaNombre);
                    provincia.setPais(pais);
                    provincia = provinciaRepository.save(provincia);
                }

                Localidad localidad = localidadRepository.findByNombre(localidadNombre);
                if (localidad == null) {
                    localidad = new Localidad();
                    localidad.setId(localidadId);
                    localidad.setNombre(localidadNombre);
                    localidad.setProvincia(provincia);
                    localidadRepository.save(localidad);
                }
            });

            // Crear 1 empresa
            ImagenEmpresa imagenEmpresa = ImagenEmpresa.builder().url("https://media.timeout.com/images/105790129/1920/1440/image.jpg").build();
            Empresa empresaBuenSabor = Empresa.builder().eliminado(false).nombre("El Buen Sabor").domain("elbuensabor.com").cuil(30503167).imagen(imagenEmpresa).razonSocial("Venta de Alimentos").build();
            empresaRepository.save(empresaBuenSabor);
            logger.info("Empresa {}", empresaBuenSabor);

            Empresa empresaBrown = Empresa.builder().eliminado(false).nombre("Lo de Brown").domain("lodebrown.com").cuil(30503167).imagen(ImagenEmpresa.builder().url("https://economipedia.com/wp-content/uploads/Empresa-1.png").build()).razonSocial("Empresa de negocios").build();
            empresaRepository.save(empresaBrown);
            logger.info("Empresa {}", empresaBrown);

            // Crear 2 sucursales para esa empresa
            ImagenSucursal imagenSucursalChacras = ImagenSucursal.builder().url("https://mendoza-camara.org/wp-content/uploads/2021/11/Iglesia-Perpetuo-Socorro-1.jpg").build();
            Sucursal sucursalChacras = Sucursal.builder().eliminado(false).nombre("En chacras").imagen(imagenSucursalChacras).casaMatriz(true).empresa(empresaBuenSabor).build();
            Domicilio domicilioViamonte = Domicilio.builder().cp(5509).calle("Viamonte").numero(500).localidad(localidad1).build();
            sucursalChacras.setDomicilio(domicilioViamonte);
            logger.info("Sucursal {}", sucursalChacras);

            ImagenSucursal imagenSucursalGodoyCruz = ImagenSucursal.builder().url("https://dynamic-media-cdn.tripadvisor.com/media/photo-o/24/06/05/3f/caption.jpg?w=300&h=300&s=1").build();
            Sucursal sucursalGodoyCruz = Sucursal.builder().eliminado(false).nombre("En godoy cruz").imagen(imagenSucursalGodoyCruz).casaMatriz(false).empresa(empresaBuenSabor).build();
            Domicilio domicilioSanMartin = Domicilio.builder().cp(5511).calle("San Martin").numero(789).localidad(localidad2).build();
            sucursalGodoyCruz.setDomicilio(domicilioSanMartin);
            logger.info("Sucursal {}", sucursalGodoyCruz);

            Categoria categoriaPizzas = Categoria.builder().denominacion("Pizzas").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).build();

            logger.info("Categoría {}", categoriaPizzas);

            Categoria categoriaInsumos = Categoria.builder().denominacion("Insumos").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).build();


            logger.info("Categoría {}", categoriaInsumos);

            Categoria categoriaBebidas = Categoria.builder().denominacion("Bebidas").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).build();
            Categoria categoriaBebidasConAlcohol = Categoria.builder().denominacion("Bebidas con alcohol").categoriaPadre(categoriaBebidas).sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).build();
            Categoria categoriaBebidasSinAlcohol = Categoria.builder().denominacion("Bebidas sin alcohol").categoriaPadre(categoriaBebidas).sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).build();
            Categoria categoriaTragos = Categoria.builder().denominacion("Tragos").categoriaPadre(categoriaBebidasConAlcohol).sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).build();
            Categoria categoriaGaseosas = Categoria.builder().denominacion("Gaseosas").categoriaPadre(categoriaBebidasSinAlcohol).sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).build();
            Categoria categoriaHamburguesas = Categoria.builder().denominacion("Hamburguesas").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).build();
            Categoria categoriaCombos = Categoria.builder().denominacion("Combos").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).build();
            Categoria categoriaPapas = Categoria.builder().denominacion("Papas fritas").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).build();
            Categoria categoriaCarnesFiambres = Categoria.builder().denominacion("Carnes y fiambres").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).build();
            Categoria categoriaVegetales = Categoria.builder().denominacion("Vegetales").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).build();
            Categoria categoriaLacteos = Categoria.builder().denominacion("Lácteos").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).build();
            Categoria categoriaPanaderia = Categoria.builder().denominacion("Panadería").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).build();
            Categoria categoriaHuevos = Categoria.builder().denominacion("Huevos").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).build();
            Categoria categoriaSalsasAdheresos = Categoria.builder().denominacion("Salsas y adheresos").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).build();
            Categoria categoriaEspecias = Categoria.builder().denominacion("Especias").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).build();
            Categoria categoriaLiquidos = Categoria.builder().denominacion("Líquidos").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).build();
            Categoria categoriaCarnes = Categoria.builder().denominacion("Carnes").categoriaPadre(categoriaCarnesFiambres).sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).build();
            Categoria categoriaFiambres = Categoria.builder().denominacion("Fiambres").categoriaPadre(categoriaCarnesFiambres).sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).build();
            Categoria categoriaQuesos = Categoria.builder().denominacion("Quesos").categoriaPadre(categoriaLacteos).sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).build();
            Categoria categoriaHarinas = Categoria.builder().denominacion("Harinas").categoriaPadre(categoriaPanaderia).sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).build();
            Categoria categoriaSalsas = Categoria.builder().denominacion("Salsas").categoriaPadre(categoriaSalsasAdheresos).sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).build();
            Categoria categoriaAdheresos = Categoria.builder().denominacion("Adheresos").categoriaPadre(categoriaSalsasAdheresos).sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).build();

            logger.info("Categoría {}", categoriaBebidas);
            sucursalChacras.setCategorias(Set.of(categoriaPizzas, categoriaInsumos, categoriaBebidas, categoriaBebidasConAlcohol, categoriaBebidasSinAlcohol, categoriaTragos, categoriaGaseosas, categoriaHamburguesas, categoriaCombos, categoriaPapas, categoriaCarnesFiambres, categoriaVegetales, categoriaLacteos, categoriaPanaderia, categoriaHuevos, categoriaSalsasAdheresos, categoriaEspecias, categoriaLiquidos, categoriaCarnes, categoriaFiambres, categoriaQuesos, categoriaHarinas, categoriaSalsas, categoriaAdheresos));
            sucursalGodoyCruz.setCategorias(Set.of(categoriaPizzas, categoriaInsumos, categoriaBebidas, categoriaBebidasConAlcohol, categoriaBebidasSinAlcohol, categoriaTragos, categoriaGaseosas, categoriaHamburguesas, categoriaCombos, categoriaPapas, categoriaCarnesFiambres, categoriaVegetales, categoriaLacteos, categoriaPanaderia, categoriaHuevos, categoriaSalsasAdheresos, categoriaEspecias, categoriaLiquidos, categoriaCarnes, categoriaFiambres, categoriaQuesos, categoriaHarinas, categoriaSalsas, categoriaAdheresos));

            Set.of(sucursalChacras, sucursalGodoyCruz).forEach(sucursal -> {

                for (Dia dia : Dia.values()) {
                    // Crear el conjunto de horarioDetalles para el día de la semana
                    HorarioSucursal horarioSucursal = HorarioSucursal.builder()
                            .sucursal(sucursal)
                            .diaSemana(dia)
                            .build();
                    horarioSucursal.getHorarioDetalles().add(HorarioDetalleSucursal.builder()
                            .horaInicio(LocalTime.of(20, 0))
                            .horaFin(LocalTime.of(0, 0))
                            .horario(horarioSucursal)
                            .build());
                    // Configurar el horario según el día de la semana
                    if (dia == Dia.Sabado || dia == Dia.Domingo) {
                        // Para sábados y domingos de 11:00 a 15:00
                        horarioSucursal.getHorarioDetalles().add(HorarioDetalleSucursal.builder()
                                .horaInicio(LocalTime.of(11, 0))
                                .horaFin(LocalTime.of(15, 0))
                                .horario(horarioSucursal)
                                .build());
                    }

                    if (sucursal == sucursalChacras) {
                        if (dia != Dia.Sabado && dia != Dia.Domingo) {
                            // Para sábados y domingos de 09:00 a 17:00
                            horarioSucursal.getHorarioDetalles().add(HorarioDetalleSucursal.builder()
                                    .horaInicio(LocalTime.of(9, 0))
                                    .horaFin(LocalTime.of(17, 0))
                                    .horario(horarioSucursal)
                                    .build());
                        }
                    }

                    sucursal.getHorarios().add(horarioSucursal);

                }
            });

            sucursalRepository.saveAll(List.of(sucursalChacras, sucursalGodoyCruz));

            UnidadMedida unidadMedidaLitros = UnidadMedida.builder().denominacion("Litros").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).build();
            UnidadMedida unidadMedidaGramos = UnidadMedida.builder().denominacion("Gramos").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).build();
            UnidadMedida unidadMedidaUnidad = UnidadMedida.builder().denominacion("Unidad").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).build();
            UnidadMedida unidadMedidaPorciones = UnidadMedida.builder().denominacion("Porciones").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).build();
            UnidadMedida unidadMedidaMililitros = UnidadMedida.builder().denominacion("Mililitros").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).build();

            unidadMedidaRepository.save(unidadMedidaLitros);
            logger.info("UnidadMedida {}", unidadMedidaLitros);
            unidadMedidaRepository.save(unidadMedidaGramos);
            logger.info("UnidadMedida {}", unidadMedidaGramos);
            unidadMedidaRepository.save(unidadMedidaUnidad);
            logger.info("UnidadMedida {}", unidadMedidaUnidad);
            unidadMedidaRepository.save(unidadMedidaPorciones);
            logger.info("UnidadMedida {}", unidadMedidaPorciones);
            unidadMedidaRepository.save(unidadMedidaMililitros);
            logger.info("UnidadMedida {}", unidadMedidaMililitros);


            // Crear Unidades de medida
            List<String> denominaciones = List.of(
                    "Centilitros", "Metros cúbicos", "Centímetros cúbicos", "Kilogramos",
                    "Metros", "Centímetros", "Milímetros", "Metros cuadrados", "Paquete",
                    "Docena"
            );

            Set<UnidadMedida> unidadesMedida = denominaciones.stream()
                    .map(denominacion -> UnidadMedida.builder().denominacion(denominacion).sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).build())
                    .collect(Collectors.toSet());

            unidadMedidaRepository.saveAll(unidadesMedida);

            unidadesMedida.forEach(unidadMedida -> logger.info("UnidadMedida {}", unidadMedida));

            List<String> nombres = List.of(
                    "Carne molida", "Panceta", "Pepperoni", "Lechuga", "Cebolla", "Champiñones", "Jalapeño",
                    "Cebolla roja", "Pimiento", "Papas", "Ajo", "Aceitunas verdes", "Leche", "Manteca", "Queso cheddar",
                    "Queso parmesano", "Levadura", "Ketchup", "Mayonesa", "Salsa picante",
                    "Salsa de tomate", "Sal", "Azúcar", "Pimienta", "Ajo en polvo", "Orégano", "Agua", "Aceite de oliva",
                    "Aceite vegetal", "Coca-cola 500mL", "Coca-cola cero 500mL",
                    "Sprite 500mL", "Fanta 500mL", "Schweppes 500mL",
                    "Huevos"
            );

            List<Categoria> categorias = List.of(
                    categoriaCarnes, categoriaFiambres, categoriaFiambres, categoriaVegetales,
                    categoriaVegetales, categoriaVegetales, categoriaVegetales, categoriaVegetales, categoriaVegetales,
                    categoriaVegetales, categoriaVegetales, categoriaVegetales, categoriaLacteos, categoriaLacteos,
                    categoriaQuesos, categoriaQuesos, categoriaPanaderia,
                    categoriaAdheresos, categoriaAdheresos, categoriaSalsas, categoriaSalsas, categoriaEspecias,
                    categoriaEspecias, categoriaEspecias, categoriaEspecias, categoriaEspecias, categoriaLiquidos,
                    categoriaLiquidos, categoriaLiquidos, categoriaGaseosas, categoriaGaseosas, categoriaGaseosas,
                    categoriaGaseosas, categoriaGaseosas, categoriaHuevos
            );

            List<UnidadMedida> ListUnidadesMedida = List.of(
                    unidadMedidaGramos, unidadMedidaGramos, unidadMedidaGramos, unidadMedidaGramos,
                    unidadMedidaGramos, unidadMedidaGramos, unidadMedidaGramos, unidadMedidaGramos, unidadMedidaGramos,
                    unidadMedidaGramos, unidadMedidaGramos, unidadMedidaGramos, unidadMedidaMililitros, unidadMedidaGramos,
                    unidadMedidaGramos, unidadMedidaGramos, unidadMedidaGramos,
                    unidadMedidaGramos, unidadMedidaGramos, unidadMedidaGramos, unidadMedidaGramos, unidadMedidaGramos,
                    unidadMedidaGramos, unidadMedidaGramos, unidadMedidaGramos, unidadMedidaGramos, unidadMedidaMililitros,
                    unidadMedidaMililitros, unidadMedidaMililitros, unidadMedidaUnidad, unidadMedidaUnidad, unidadMedidaUnidad,
                    unidadMedidaUnidad, unidadMedidaUnidad, unidadMedidaUnidad
            );

            List<Double> preciosCompra = List.of(
                    2000.0 / 1000, 5000.0 / 1000, 5000.0 / 1000, 400.0 / 1000,
                    300.0 / 1000, 700.0 / 1000, 700.0 / 1000, 400.0 / 1000, 600.0 / 1000,
                    400.0 / 1000, 2000.0 / 1000, 3500.0 / 1000, 450.0 / 1000, 1500.0 / 1000,
                    4000.0 / 1000, 5000.0 / 1000, 1400.0 / 1000,
                    1200.0 / 1000, 1200.0 / 1000, 3000.0 / 1000, 800.0 / 1000, 800.0 / 1000,
                    500.0 / 1000, 1500.0 / 1000, 900.0 / 1000, 1000.0 / 1000, 600.0 / 1000,
                    3500.0 / 1000, 1200.0 / 1000, 1500.0, 1500.0, 1500.0,
                    1500.0, 1500.0, 65.0
            );

            List<Integer> minStock = List.of(
                    3000, 2500, 2500, 2000,
                    2000, 2000, 2000, 2000, 2000,
                    7000, 2000, 2000, 10000, 5000,
                    6000, 5000, 3000,
                    3000, 3000, 2000, 8000, 3000,
                    2000, 2000, 2000, 3000, 10000,
                    6000, 20000, 40, 40, 40,
                    40, 40, 40
            );

            List<Integer> actualStock = List.of(
                    3000, 3000, 3000, 3000,
                    3000, 3000, 3000, 3000, 3000,
                    9000, 3000, 3000, 11000, 6000,
                    8000, 7000, 4000,
                    4000, 4000, 3000, 9000, 4000,
                    3000, 3000, 3000, 4000, 11000,
                    8000, 25000, 50, 50, 50,
                    50, 50, 50
            );

            for (int i = 0; i < nombres.size(); i++) {
                ArticuloInsumo insumo = ArticuloInsumo.builder()
                        .denominacion(nombres.get(i))
                        .unidadMedida(ListUnidadesMedida.get(i))
                        .esParaElaborar(true)
                        .categoria(categorias.get(i))
                        .precioCompra(preciosCompra.get(i))
                        .precioVenta(preciosCompra.get(i) * 1.25)
                        .build();

                if (List.of(29, 30, 31, 32, 33, 35).contains(i)) {
                    insumo.setEsParaElaborar(false);
                }

                switch (i) {
                    case 29:
                        insumo.getImagenes().add(
                                ImagenArticulo.builder().url("https://github.com/Sebass24/El-Buen-Sabor-Frontend/blob/main/ElbuenSaborReactV1/public/Images/coca-cola-500.jpg?raw=true")
                                        .articulo(insumo).build());
                        break;
                    case 30:
                        insumo.getImagenes().add(
                                ImagenArticulo.builder().url("https://github.com/Sebass24/El-Buen-Sabor-Frontend/blob/main/ElbuenSaborReactV1/public/Images/coca-cola-zero-500.jpg?raw=true")
                                        .articulo(insumo).build());
                        break;
                    case 31:
                        insumo.getImagenes().add(
                                ImagenArticulo.builder().url("https://github.com/Sebass24/El-Buen-Sabor-Frontend/blob/main/ElbuenSaborReactV1/public/Images/sprite-500.png?raw=true")
                                        .articulo(insumo).build());
                        break;
                    case 32:
                        insumo.getImagenes().add(
                                ImagenArticulo.builder().url("https://github.com/Sebass24/El-Buen-Sabor-Frontend/blob/main/ElbuenSaborReactV1/public/Images/fanta-500.png?raw=true")
                                        .articulo(insumo).build());
                        break;
                    case 33:
                        insumo.getImagenes().add(
                                ImagenArticulo.builder().url("https://github.com/Sebass24/El-Buen-Sabor-Frontend/blob/main/ElbuenSaborReactV1/public/Images/schweppes-500.jpeg?raw=true")
                                        .articulo(insumo).build());
                        break;
                    default:
                        break;
                }

                StockInsumo stock1 = StockInsumo.builder()
                        .articuloInsumo(insumo)
                        .stockActual(actualStock.get(i))
                        .stockMinimo(minStock.get(i))
                        .stockMaximo(minStock.get(i) * 2)
                        .sucursal(sucursalChacras)
                        .build();

                StockInsumo stock2 = StockInsumo.builder()
                        .articuloInsumo(insumo)
                        .stockActual(actualStock.get(i) * 2)
                        .stockMinimo(minStock.get(i))
                        .stockMaximo(minStock.get(i) * 2)
                        .sucursal(sucursalGodoyCruz)
                        .build();

                insumo.getStocksInsumo().add(stock1);
                insumo.getStocksInsumo().add(stock2);

                logger.info("Saving insumo: {}", insumo);
                articuloInsumoRepository.save(insumo);
            }

            //Crear Insumos , coca cola , harina , etc
            ArticuloInsumo cocaCola = ArticuloInsumo.builder().denominacion("Coca cola 1,5L").unidadMedida(unidadMedidaUnidad).esParaElaborar(false).categoria(categoriaGaseosas).precioCompra(2000.0).precioVenta(2500.0).build();
            ImagenArticulo imagenCoca = ImagenArticulo.builder().url("https://www.rimoldimayorista.com.ar/datos/uploads/mod_catalogo/31308/coca-1-5-605e30445448a.jpg").articulo(cocaCola).build();
            cocaCola.getImagenes().add(imagenCoca);
            logger.info("Insumo {}", cocaCola);

            StockInsumo stockCocacola1 = StockInsumo.builder().articuloInsumo(cocaCola).stockActual(100).stockMinimo(50).stockMaximo(300).sucursal(sucursalChacras).build();
            StockInsumo stockCocacola2 = StockInsumo.builder().articuloInsumo(cocaCola).stockActual(80).stockMinimo(50).stockMaximo(300).sucursal(sucursalGodoyCruz).build();
            cocaCola.getStocksInsumo().add(stockCocacola1);
            cocaCola.getStocksInsumo().add(stockCocacola2);

            ArticuloInsumo harina = ArticuloInsumo.builder().denominacion("Harina").unidadMedida(unidadMedidaGramos).esParaElaborar(true).categoria(categoriaPanaderia).precioCompra(0.5).precioVenta(0.6).build();
            ImagenArticulo imagenHarina = ImagenArticulo.builder().url("https://mandolina.co/wp-content/uploads/2023/03/648366622-1024x683.jpg").articulo(harina).build();
            harina.getImagenes().add(imagenHarina);
            logger.info("Insumo {}", harina);

            StockInsumo stockharina1 = StockInsumo.builder().articuloInsumo(harina).stockActual(20000).stockMinimo(5000).stockMaximo(50000).sucursal(sucursalChacras).build();
            StockInsumo stockharina2 = StockInsumo.builder().articuloInsumo(harina).stockActual(10000).stockMinimo(5000).stockMaximo(50000).sucursal(sucursalGodoyCruz).build();
            harina.getStocksInsumo().add(stockharina1);
            harina.getStocksInsumo().add(stockharina2);

            ArticuloInsumo tomate = ArticuloInsumo.builder().denominacion("Tomate").unidadMedida(unidadMedidaGramos).esParaElaborar(true).categoria(categoriaVegetales).precioCompra(0.08).precioVenta(0.09).build();
            ImagenArticulo imagenTomate = ImagenArticulo.builder().url("https://thefoodtech.com/wp-content/uploads/2020/06/Componentes-de-calidad-en-el-tomate-828x548.jpg").articulo(tomate).build();
            tomate.getImagenes().add(imagenTomate);
            logger.info("Insumo {}", tomate);

            StockInsumo stocktomate1 = StockInsumo.builder().articuloInsumo(tomate).stockActual(10000).stockMinimo(8000).stockMaximo(50000).sucursal(sucursalChacras).build();
            StockInsumo stocktomate2 = StockInsumo.builder().articuloInsumo(tomate).stockActual(10000).stockMinimo(8000).stockMaximo(50000).sucursal(sucursalGodoyCruz).build();
            tomate.getStocksInsumo().add(stocktomate1);
            tomate.getStocksInsumo().add(stocktomate2);

            ArticuloInsumo queso = ArticuloInsumo.builder().denominacion("Queso mozzarella").unidadMedida(unidadMedidaGramos).esParaElaborar(true).categoria(categoriaQuesos).precioCompra(2.5).precioVenta(2.7).build();
            ImagenArticulo imagenQueso = ImagenArticulo.builder().url("https://superdepaso.com.ar/wp-content/uploads/2021/06/SANTAROSA-PATEGRAS-04.jpg").articulo(queso).build();
            queso.getImagenes().add(imagenQueso);
            logger.info("Insumo {}", queso);


            StockInsumo stockqueso1 = StockInsumo.builder().articuloInsumo(queso).stockActual(100000).stockMinimo(10000).stockMaximo(500000).sucursal(sucursalChacras).build();
            StockInsumo stockqueso2 = StockInsumo.builder().articuloInsumo(queso).stockActual(70000).stockMinimo(10000).stockMaximo(200000).sucursal(sucursalGodoyCruz).build();
            queso.getStocksInsumo().add(stockqueso1);
            queso.getStocksInsumo().add(stockqueso2);

            // Crear Articulos Manufacturados HamburguesaClasica
            ArticuloManufacturado HamburguesaClasica = ArticuloManufacturado.builder().denominacion("Hamburguesa clásica").descripcion("Este producto es un arte culinario. Exquisito! Acá en el Buen Sabor solo ofrecemos lo mejor de lo mejor, solo para vos. Matate el hambre, dejate el buen sabor de boca.").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).resumen("Hamburguesa de 100g de carne molida, panceta, queso cheddar, tomate, lechuga, cebolla, ketchup y mayonesa.").unidadMedida(unidadMedidaUnidad).precioVenta(2300.0).tiempoEstimadoMinutos(15)
                    .preparacion("Precalentar una sartén a fuego medio-alto.\n" +
                            "Salpimentar la carne y hacer una hamburguesa con ella.\n" +
                            "Cocinar la hamburguesa durante 3-4 minutos por cada lado.\n" +
                            "Añadir la panceta a la sartén y cocinar durante 2-3 minutos por cada lado.\n" +
                            "Tostar ligeramente el pan para hamburguesa.\n" +
                            "Montar la hamburguesa: colocar la hamburguesa sobre la base del pan, agregar el queso cheddar, la panceta, el tomate, la lechuga y la cebolla.\n" +
                            "Agregar la ketchup y la mayonesa en la parte superior del pan y cubrir con la otra mitad del pan.").categoria(categoriaHamburguesas).build();
            HamburguesaClasica.getImagenes().add(ImagenArticulo.builder().url("https://github.com/Sebass24/El-Buen-Sabor-Frontend/blob/main/ElbuenSaborReactV1/public/Images/burger-classic.jpg?raw=true").articulo(HamburguesaClasica).build());
            HamburguesaClasica.setArticuloManufacturadoDetalles(Set.of(ArticuloManufacturadoDetalle.builder().cantidad(120d).articuloInsumo(articuloInsumoRepository.getById(37L)).articuloManufacturado(HamburguesaClasica).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(3d).articuloInsumo(articuloInsumoRepository.getById(22L)).articuloManufacturado(HamburguesaClasica).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(1.5d).articuloInsumo(articuloInsumoRepository.getById(17L)).articuloManufacturado(HamburguesaClasica).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(60d).articuloInsumo(articuloInsumoRepository.getById(27L)).articuloManufacturado(HamburguesaClasica).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(60d).articuloInsumo(articuloInsumoRepository.getById(13L)).articuloManufacturado(HamburguesaClasica).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(7.5d).articuloInsumo(articuloInsumoRepository.getById(14L)).articuloManufacturado(HamburguesaClasica).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(6.5d).articuloInsumo(articuloInsumoRepository.getById(23L)).articuloManufacturado(HamburguesaClasica).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(100d).articuloInsumo(articuloInsumoRepository.getById(1L)).articuloManufacturado(HamburguesaClasica).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(30d).articuloInsumo(articuloInsumoRepository.getById(2L)).articuloManufacturado(HamburguesaClasica).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(60d).articuloInsumo(articuloInsumoRepository.getById(38L)).articuloManufacturado(HamburguesaClasica).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(30d).articuloInsumo(articuloInsumoRepository.getById(4L)).articuloManufacturado(HamburguesaClasica).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(30d).articuloInsumo(articuloInsumoRepository.getById(5L)).articuloManufacturado(HamburguesaClasica).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(20d).articuloInsumo(articuloInsumoRepository.getById(15L)).articuloManufacturado(HamburguesaClasica).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(15d).articuloInsumo(articuloInsumoRepository.getById(18L)).articuloManufacturado(HamburguesaClasica).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(15d).articuloInsumo(articuloInsumoRepository.getById(19L)).articuloManufacturado(HamburguesaClasica).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(0.2d).articuloInsumo(articuloInsumoRepository.getById(24L)).articuloManufacturado(HamburguesaClasica).build()));
            // Crear Articulos Manufacturados HamburguesaChampinones
            ArticuloManufacturado HamburguesaChampinones = ArticuloManufacturado.builder().denominacion("Hamburguesa con champiñones").descripcion("Este producto es un arte culinario. Exquisito! Acá en el Buen Sabor solo ofrecemos lo mejor de lo mejor, solo para vos. Matate el hambre, dejate el buen sabor de boca.").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).resumen("Hamburguesa de 100g de carne molida, panceta, tomate, lechuga, cebolla, queso mozzarella, champiñones salteados, ketchup y mayonesa.").unidadMedida(unidadMedidaUnidad).precioVenta(2800.0).tiempoEstimadoMinutos(15)
                    .preparacion("Precalentar una sartén a fuego medio-alto.\n" +
                            "Salpimentar la carne y hacer una hamburguesa con ella.\n" +
                            "Cocinar la hamburguesa durante 3-4 minutos por cada lado.\n" +
                            "Añadir la panceta a la sartén y cocinar durante 2-3 minutos por cada lado.\n" +
                            "Tostar ligeramente el pan para hamburguesa.\n" +
                            "Cortar los champiñones en rodajas y saltear en la sartén hasta que estén dorados.\n" +
                            "Montar la hamburguesa: colocar la hamburguesa sobre la base del pan, agregar el queso mozzarella, la panceta, el tomate, la lechuga, la cebolla y los champiñones.\n" +
                            "Agregar la ketchup y la mayonesa en la parte superior del pan y cubrir con la otra mitad del pan.").categoria(categoriaHamburguesas).build();
            HamburguesaChampinones.getImagenes().add(ImagenArticulo.builder().url("https://github.com/Sebass24/El-Buen-Sabor-Frontend/blob/main/ElbuenSaborReactV1/public/Images/burger-mushroom-bacon.jpg?raw=true").articulo(HamburguesaChampinones).build());
            HamburguesaChampinones.setArticuloManufacturadoDetalles(Set.of(ArticuloManufacturadoDetalle.builder().cantidad(120d).articuloInsumo(articuloInsumoRepository.getById(37L)).articuloManufacturado(HamburguesaChampinones).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(3d).articuloInsumo(articuloInsumoRepository.getById(22L)).articuloManufacturado(HamburguesaChampinones).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(1.5d).articuloInsumo(articuloInsumoRepository.getById(17L)).articuloManufacturado(HamburguesaChampinones).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(60d).articuloInsumo(articuloInsumoRepository.getById(27L)).articuloManufacturado(HamburguesaChampinones).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(60d).articuloInsumo(articuloInsumoRepository.getById(13L)).articuloManufacturado(HamburguesaChampinones).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(7.5d).articuloInsumo(articuloInsumoRepository.getById(14L)).articuloManufacturado(HamburguesaChampinones).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(6.5d).articuloInsumo(articuloInsumoRepository.getById(23L)).articuloManufacturado(HamburguesaChampinones).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(100d).articuloInsumo(articuloInsumoRepository.getById(1L)).articuloManufacturado(HamburguesaChampinones).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(30d).articuloInsumo(articuloInsumoRepository.getById(2L)).articuloManufacturado(HamburguesaChampinones).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(60d).articuloInsumo(articuloInsumoRepository.getById(38L)).articuloManufacturado(HamburguesaChampinones).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(30d).articuloInsumo(articuloInsumoRepository.getById(4L)).articuloManufacturado(HamburguesaChampinones).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(30d).articuloInsumo(articuloInsumoRepository.getById(5L)).articuloManufacturado(HamburguesaChampinones).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(20d).articuloInsumo(articuloInsumoRepository.getById(39L)).articuloManufacturado(HamburguesaChampinones).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(15d).articuloInsumo(articuloInsumoRepository.getById(18L)).articuloManufacturado(HamburguesaChampinones).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(15d).articuloInsumo(articuloInsumoRepository.getById(19L)).articuloManufacturado(HamburguesaChampinones).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(0.2d).articuloInsumo(articuloInsumoRepository.getById(24L)).articuloManufacturado(HamburguesaChampinones).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(20d).articuloInsumo(articuloInsumoRepository.getById(6L)).articuloManufacturado(HamburguesaChampinones).build()));

            // Crear Articulos Manufacturados HamburguesaPicante
            ArticuloManufacturado HamburguesaPicante = ArticuloManufacturado.builder().denominacion("Hamburguesa picante").descripcion("Este producto es un arte culinario. Exquisito! Acá en el Buen Sabor solo ofrecemos lo mejor de lo mejor, solo para vos. Matate el hambre, dejate el buen sabor de boca.").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).resumen("Hamburguesa de 100g de carne molida, queso cheddar, jalapeños frescos, cebolla roja, tomate, lechuga y salsa picante-mayonesa.").unidadMedida(unidadMedidaUnidad).precioVenta(3000.0).tiempoEstimadoMinutos(15)
                    .preparacion("En un tazón, mezcla la carne molida con el ajo en polvo, sal y pimienta. Forma una hamburguesa con la mezcla.\n" +
                            "Cocina la hamburguesa a la parrilla o en una sartén con un poco de aceite hasta que esté dorada por ambos lados y bien cocida en el centro.\n" +
                            "Agrega la rebanada de queso encima de la hamburguesa para que se derrita.\n" +
                            "En un tazón pequeño, mezcla la salsa picante y la mayonesa para hacer una salsa picante.\n" +
                            "Tuesta ligeramente el pan de hamburguesa.\n" +
                            "Coloca la hamburguesa en el pan tostado, agrega el jalapeño fresco, cebolla, tomate y hoja de lechuga.\n" +
                            "Agrega la salsa picante y mayonesa al gusto.").categoria(categoriaHamburguesas).build();
            HamburguesaPicante.getImagenes().add(ImagenArticulo.builder().url("https://github.com/Sebass24/El-Buen-Sabor-Frontend/blob/main/ElbuenSaborReactV1/public/Images/burger-spicy.jpg?raw=true").articulo(HamburguesaPicante).build());


            HamburguesaPicante.setArticuloManufacturadoDetalles(Set.of(ArticuloManufacturadoDetalle.builder().cantidad(120d).articuloInsumo(articuloInsumoRepository.getById(37L)).articuloManufacturado(HamburguesaPicante).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(3d).articuloInsumo(articuloInsumoRepository.getById(22L)).articuloManufacturado(HamburguesaPicante).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(1.5d).articuloInsumo(articuloInsumoRepository.getById(17L)).articuloManufacturado(HamburguesaPicante).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(60d).articuloInsumo(articuloInsumoRepository.getById(27L)).articuloManufacturado(HamburguesaPicante).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(60d).articuloInsumo(articuloInsumoRepository.getById(13L)).articuloManufacturado(HamburguesaPicante).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(7.5d).articuloInsumo(articuloInsumoRepository.getById(14L)).articuloManufacturado(HamburguesaPicante).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(6.5d).articuloInsumo(articuloInsumoRepository.getById(23L)).articuloManufacturado(HamburguesaPicante).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(100d).articuloInsumo(articuloInsumoRepository.getById(1L)).articuloManufacturado(HamburguesaPicante).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(60d).articuloInsumo(articuloInsumoRepository.getById(38L)).articuloManufacturado(HamburguesaPicante).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(30d).articuloInsumo(articuloInsumoRepository.getById(4L)).articuloManufacturado(HamburguesaPicante).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(30d).articuloInsumo(articuloInsumoRepository.getById(5L)).articuloManufacturado(HamburguesaPicante).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(20d).articuloInsumo(articuloInsumoRepository.getById(39L)).articuloManufacturado(HamburguesaPicante).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(15d).articuloInsumo(articuloInsumoRepository.getById(19L)).articuloManufacturado(HamburguesaPicante).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(0.2d).articuloInsumo(articuloInsumoRepository.getById(24L)).articuloManufacturado(HamburguesaPicante).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(10d).articuloInsumo(articuloInsumoRepository.getById(7L)).articuloManufacturado(HamburguesaPicante).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(15d).articuloInsumo(articuloInsumoRepository.getById(20L)).articuloManufacturado(HamburguesaPicante).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(3d).articuloInsumo(articuloInsumoRepository.getById(25L)).articuloManufacturado(HamburguesaPicante).build()
            ));

            // Crear Articulos Manufacturados HamburguesaChedarChampinones
            ArticuloManufacturado HamburguesaChedarChampinones = ArticuloManufacturado.builder().denominacion("Hamburguesa de champiñones y queso cheddar").descripcion("Este producto es un arte culinario. Exquisito! Acá en el Buen Sabor solo ofrecemos lo mejor de lo mejor, solo para vos. Matate el hambre, dejate el buen sabor de boca.").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).resumen("Hamburguesa de 100g de carne molida, queso cheddar y champiñones frescos y cebolla caramelizada.").unidadMedida(unidadMedidaUnidad).precioVenta(3000.0).tiempoEstimadoMinutos(15)
                    .preparacion("Lavar los champiñones y cortarlos en rebanadas finas.\n" +
                            "En una sartén, calentar el aceite de oliva y agregar los champiñones y la cebolla picada. Cocinar por unos minutos hasta que estén dorados y suaves.\n" +
                            "Mientras tanto, sazonar la carne molida con sal y pimienta y formar una hamburguesa.\n" +
                            "Cocinar la hamburguesa en una sartén o en una parrilla durante 4-5 minutos de cada lado.\n" +
                            "Cuando la hamburguesa esté casi lista, colocar la rebanada de queso cheddar encima para que se derrita.\n" +
                            "Armar la hamburguesa colocando la hamburguesa con queso sobre la base del pan, y luego los champiñones salteados. Colocar la tapa del pan encima.").categoria(categoriaHamburguesas).build();
            HamburguesaChedarChampinones.getImagenes().add(ImagenArticulo.builder().url("https://github.com/Sebass24/El-Buen-Sabor-Frontend/blob/main/ElbuenSaborReactV1/public/Images/burger-mushrooms-cheese.jpg?raw=true").articulo(HamburguesaChedarChampinones).build());
            HamburguesaChedarChampinones.setArticuloManufacturadoDetalles(Set.of(
                    ArticuloManufacturadoDetalle.builder().cantidad(120d).articuloInsumo(articuloInsumoRepository.getById(37L)).articuloManufacturado(HamburguesaChedarChampinones).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(3d).articuloInsumo(articuloInsumoRepository.getById(22L)).articuloManufacturado(HamburguesaChedarChampinones).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(1.5d).articuloInsumo(articuloInsumoRepository.getById(17L)).articuloManufacturado(HamburguesaChedarChampinones).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(60d).articuloInsumo(articuloInsumoRepository.getById(27L)).articuloManufacturado(HamburguesaChedarChampinones).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(60d).articuloInsumo(articuloInsumoRepository.getById(13L)).articuloManufacturado(HamburguesaChedarChampinones).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(7.5d).articuloInsumo(articuloInsumoRepository.getById(14L)).articuloManufacturado(HamburguesaChedarChampinones).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(6.5d).articuloInsumo(articuloInsumoRepository.getById(25L)).articuloManufacturado(HamburguesaChedarChampinones).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(120d).articuloInsumo(articuloInsumoRepository.getById(1L)).articuloManufacturado(HamburguesaChedarChampinones).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(50d).articuloInsumo(articuloInsumoRepository.getById(6L)).articuloManufacturado(HamburguesaChedarChampinones).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(20d).articuloInsumo(articuloInsumoRepository.getById(15L)).articuloManufacturado(HamburguesaChedarChampinones).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(25d).articuloInsumo(articuloInsumoRepository.getById(5L)).articuloManufacturado(HamburguesaChedarChampinones).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(5d).articuloInsumo(articuloInsumoRepository.getById(28L)).articuloManufacturado(HamburguesaChedarChampinones).build()
                    , ArticuloManufacturadoDetalle.builder().cantidad(0.2d).articuloInsumo(articuloInsumoRepository.getById(24L)).articuloManufacturado(HamburguesaChedarChampinones).build()));

            // Crear Articulos Manufacturados HamburguesaPancetaHuevo
            ArticuloManufacturado HamburguesaPancetaHuevo = ArticuloManufacturado.builder().denominacion("Hamburguesa de panceta y huevo frito").descripcion("Este producto es un arte culinario. Exquisito! Acá en el Buen Sabor solo ofrecemos lo mejor de lo mejor, solo para vos. Matate el hambre, dejate el buen sabor de boca.").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).resumen("Hamburguesa de 100g de carne molida, panceta crujiente, huevo frito, lechuga y tomate.").unidadMedida(unidadMedidaUnidad).precioVenta(2800.0).tiempoEstimadoMinutos(15)
                    .preparacion("Cocinar la panceta en una sartén hasta que esté dorado y crujiente. Reservar.\n" +
                            "Formar una hamburguesa con la carne molida y sazonar con sal y pimienta.\n" +
                            "En la misma sartén donde se cocinó el panceta, añadir la mantequilla y cocinar la hamburguesa durante 4-5 minutos de cada lado.\n" +
                            "Mientras tanto, freír un huevo en otra sartén con un poco de aceite. Reservar.\n" +
                            "Armar la hamburguesa colocando la hoja de lechuga y la rodaja de tomate sobre la base del pan. Colocar la hamburguesa cocida encima, seguida por el panceta y el huevo frito. Colocar la tapa del pan encima y servir.").categoria(categoriaHamburguesas).build();
            HamburguesaPancetaHuevo.getImagenes().add(ImagenArticulo.builder().url("https://github.com/Sebass24/El-Buen-Sabor-Frontend/blob/main/ElbuenSaborReactV1/public/Images/burger-bacon-egg.jpg?raw=true").articulo(HamburguesaPancetaHuevo).build());
            HamburguesaPancetaHuevo.setArticuloManufacturadoDetalles(Set.of(ArticuloManufacturadoDetalle.builder().cantidad(120d).articuloInsumo(articuloInsumoRepository.getById(37L)).articuloManufacturado(HamburguesaPancetaHuevo).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(3d).articuloInsumo(articuloInsumoRepository.getById(22L)).articuloManufacturado(HamburguesaPancetaHuevo).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(1.5d).articuloInsumo(articuloInsumoRepository.getById(17L)).articuloManufacturado(HamburguesaPancetaHuevo).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(60d).articuloInsumo(articuloInsumoRepository.getById(27L)).articuloManufacturado(HamburguesaPancetaHuevo).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(60d).articuloInsumo(articuloInsumoRepository.getById(13L)).articuloManufacturado(HamburguesaPancetaHuevo).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(12.5d).articuloInsumo(articuloInsumoRepository.getById(14L)).articuloManufacturado(HamburguesaPancetaHuevo).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(6.5d).articuloInsumo(articuloInsumoRepository.getById(25L)).articuloManufacturado(HamburguesaPancetaHuevo).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(120d).articuloInsumo(articuloInsumoRepository.getById(1L)).articuloManufacturado(HamburguesaPancetaHuevo).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(1d).articuloInsumo(articuloInsumoRepository.getById(35L)).articuloManufacturado(HamburguesaPancetaHuevo).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(15d).articuloInsumo(articuloInsumoRepository.getById(2L)).articuloManufacturado(HamburguesaPancetaHuevo).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(10d).articuloInsumo(articuloInsumoRepository.getById(4L)).articuloManufacturado(HamburguesaPancetaHuevo).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(20d).articuloInsumo(articuloInsumoRepository.getById(38L)).articuloManufacturado(HamburguesaPancetaHuevo).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(0.2d).articuloInsumo(articuloInsumoRepository.getById(24L)).articuloManufacturado(HamburguesaPancetaHuevo).build()
            ));

            ArticuloManufacturado pizzaMuzarella = ArticuloManufacturado.builder()
                    .denominacion("Pizza Muzarella")
                    .descripcion("Este producto es un arte culinario. Exquisito! Acá en el Buen Sabor solo ofrecemos lo mejor de lo mejor, solo para vos. Matate el hambre, dejate el buen sabor de boca.").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).resumen("Salsa de tomate, queso mozzarella").unidadMedida(unidadMedidaUnidad).precioVenta(2500.0).tiempoEstimadoMinutos(15)
                    .preparacion("Precalentar el horno a 220°C.\n" +
                            "Estirar la masa de pizza y colocarla en una bandeja para hornear.\n" +
                            "Distribuir la salsa de tomate sobre la masa, dejando un borde de 1 cm sin cubrir.\n" +
                            "Agregar el queso mozzarella rallado sobre la salsa de tomate.\n" +
                            "Hornear la pizza durante 12-15 minutos, o hasta que el queso esté dorado y la masa esté crujiente.").categoria(categoriaPizzas).build();
            pizzaMuzarella.getImagenes().add(ImagenArticulo.builder().url("https://storage.googleapis.com/fitia-api-bucket/media/images/recipe_images/1002846.jpg").articulo(pizzaMuzarella).build());
            pizzaMuzarella.setArticuloManufacturadoDetalles(Set.of(
                    ArticuloManufacturadoDetalle.builder().cantidad(100d).articuloInsumo(articuloInsumoRepository.getById(21L)).articuloManufacturado(pizzaMuzarella).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(300d).articuloInsumo(harina).articuloManufacturado(pizzaMuzarella).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(250d).articuloInsumo(queso).articuloManufacturado(pizzaMuzarella).build()));
            logger.info("Manufacturado {}", pizzaMuzarella);

            ArticuloManufacturado pizzaNapolitana = ArticuloManufacturado.builder()
                    .denominacion("Pizza Napolitana")
                    .descripcion("Este producto es un arte culinario. Exquisito! Acá en el Buen Sabor solo ofrecemos lo mejor de lo mejor, solo para vos. Matate el hambre, dejate el buen sabor de boca.").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).resumen("Salsa de tomate, queso mozzarella, tomate en rodajas")
                    .unidadMedida(unidadMedidaUnidad).precioVenta(2700.0).tiempoEstimadoMinutos(15)
                    .preparacion("Precalentar el horno a 220°C.\n" +
                            "Estirar la masa de pizza y colocarla en una bandeja para hornear.\n" +
                            "Distribuir la salsa de tomate sobre la masa, dejando un borde de 1 cm sin cubrir.\n" +
                            "Agregar el queso mozzarella rallado sobre la salsa de tomate.\n" +
                            "Distribuir los rodajas de tomate sobre el queso.\n" +
                            "Hornear la pizza durante 12-15 minutos, o hasta que el queso esté dorado y la masa esté crujiente.").categoria(categoriaPizzas).build();
            pizzaNapolitana.getImagenes().add(ImagenArticulo.builder().url("https://assets.elgourmet.com/wp-content/uploads/2023/03/8metlvp345_portada-pizza-1024x686.jpg.webp").articulo(pizzaNapolitana).build());
            pizzaNapolitana.setArticuloManufacturadoDetalles(Set.of(ArticuloManufacturadoDetalle.builder().cantidad(300d).articuloInsumo(harina).articuloManufacturado(pizzaNapolitana).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(250d).articuloInsumo(queso).articuloManufacturado(pizzaNapolitana).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(100d).articuloInsumo(tomate).articuloManufacturado(pizzaNapolitana).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(100d).articuloInsumo(articuloInsumoRepository.getById(21L)).articuloManufacturado(pizzaNapolitana).build()));

            // Crear Articulos Manufacturados PizzaPepperoniChampinones
            ArticuloManufacturado PizzaPepperoniChampinones = ArticuloManufacturado.builder()
                    .denominacion("Pizza de pepperoni y champiñones")
                    .descripcion("Este producto es un arte culinario. Exquisito! Acá en el Buen Sabor solo ofrecemos lo mejor de lo mejor, solo para vos. Matate el hambre, dejate el buen sabor de boca.").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).resumen("Salsa de tomate, queso mozzarella, pepperoni y champiñones.")
                    .unidadMedida(unidadMedidaUnidad).precioVenta(3000.0).tiempoEstimadoMinutos(20)
                    .preparacion("Precalentar el horno a 220°C.\n" +
                            "Estirar la masa de pizza y colocarla en una bandeja para hornear.\n" +
                            "Distribuir la salsa de tomate sobre la masa, dejando un borde de 1 cm sin cubrir.\n" +
                            "Agregar el queso mozzarella rallado sobre la salsa de tomate.\n" +
                            "Distribuir las rodajas de pepperoni y champiñones sobre el queso.\n" +
                            "Espolvorear orégano seco sobre la pizza.\n" +
                            "Hornear la pizza durante 12-15 minutos, o hasta que el queso esté dorado y la masa esté crujiente.").categoria(categoriaPizzas).build();
            PizzaPepperoniChampinones.getImagenes().add(ImagenArticulo.builder().url("https://github.com/Sebass24/El-Buen-Sabor-Frontend/blob/main/ElbuenSaborReactV1/public/Images/pizza-pepperoni-mushroom.jpg?raw=true").articulo(PizzaPepperoniChampinones).build());
            PizzaPepperoniChampinones.setArticuloManufacturadoDetalles(Set.of(
                    ArticuloManufacturadoDetalle.builder().cantidad(250d).articuloInsumo(articuloInsumoRepository.getById(37L)).articuloManufacturado(PizzaPepperoniChampinones).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(150d).articuloInsumo(articuloInsumoRepository.getById(9L)).articuloManufacturado(PizzaPepperoniChampinones).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(5d).articuloInsumo(articuloInsumoRepository.getById(22L)).articuloManufacturado(PizzaPepperoniChampinones).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(5d).articuloInsumo(articuloInsumoRepository.getById(17L)).articuloManufacturado(PizzaPepperoniChampinones).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(15d).articuloInsumo(articuloInsumoRepository.getById(28L)).articuloManufacturado(PizzaPepperoniChampinones).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(100d).articuloInsumo(articuloInsumoRepository.getById(21L)).articuloManufacturado(PizzaPepperoniChampinones).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(150d).articuloInsumo(articuloInsumoRepository.getById(39L)).articuloManufacturado(PizzaPepperoniChampinones).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(50d).articuloInsumo(articuloInsumoRepository.getById(3L)).articuloManufacturado(PizzaPepperoniChampinones).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(50d).articuloInsumo(articuloInsumoRepository.getById(6L)).articuloManufacturado(PizzaPepperoniChampinones).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(5d).articuloInsumo(articuloInsumoRepository.getById(26L)).articuloManufacturado(PizzaPepperoniChampinones).build()
            ));


            // Crear Articulos Manufacturados PizzaCarneMolidaPimientos

            ArticuloManufacturado PizzaCarneMolidaPimientos = ArticuloManufacturado.builder()
                    .denominacion("Pizza de carne molida y pimientos")
                    .descripcion("Este producto es un arte culinario. Exquisito! Acá en el Buen Sabor solo ofrecemos lo mejor de lo mejor, solo para vos. Matate el hambre, dejate el buen sabor de boca.").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).resumen("Salsa de tomate, queso mozzarella, carne molida y pimientos.").unidadMedida(unidadMedidaUnidad).precioVenta(3500.0).tiempoEstimadoMinutos(20)
                    .preparacion("Estirar la masa de pizza y colocarla en una bandeja para hornear.\n" +
                            "Distribuir la salsa de tomate sobre la masa, dejando un borde de 1 cm sin cubrir.\n" +
                            "Agregar el queso mozzarella rallado sobre la salsa de tomate.\n" +
                            "Distribuir la carne molida y las tiras de pimiento sobre el queso.\n" +
                            "Hornear la pizza durante 12-15 minutos, o hasta que el queso esté dorado y la masa esté crujiente.").categoria(categoriaPizzas).build();
            PizzaCarneMolidaPimientos.getImagenes().add(ImagenArticulo.builder().url("https://github.com/Sebass24/El-Buen-Sabor-Frontend/blob/main/ElbuenSaborReactV1/public/Images/pizza-meat-peppers.jpg?raw=true").articulo(PizzaCarneMolidaPimientos).build());
            PizzaCarneMolidaPimientos.setArticuloManufacturadoDetalles(Set.of(
                    ArticuloManufacturadoDetalle.builder().cantidad(250d).articuloInsumo(articuloInsumoRepository.getById(37L)).articuloManufacturado(PizzaCarneMolidaPimientos).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(150d).articuloInsumo(articuloInsumoRepository.getById(9L)).articuloManufacturado(PizzaCarneMolidaPimientos).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(5d).articuloInsumo(articuloInsumoRepository.getById(22L)).articuloManufacturado(PizzaCarneMolidaPimientos).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(5d).articuloInsumo(articuloInsumoRepository.getById(17L)).articuloManufacturado(PizzaCarneMolidaPimientos).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(15d).articuloInsumo(articuloInsumoRepository.getById(28L)).articuloManufacturado(PizzaCarneMolidaPimientos).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(100d).articuloInsumo(articuloInsumoRepository.getById(21L)).articuloManufacturado(PizzaCarneMolidaPimientos).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(150d).articuloInsumo(articuloInsumoRepository.getById(39L)).articuloManufacturado(PizzaCarneMolidaPimientos).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(100d).articuloInsumo(articuloInsumoRepository.getById(1L)).articuloManufacturado(PizzaCarneMolidaPimientos).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(70d).articuloInsumo(articuloInsumoRepository.getById(9L)).articuloManufacturado(PizzaCarneMolidaPimientos).build()
            ));
            // Crear Articulos Manufacturados PizzaCebollaPanceta

            ArticuloManufacturado PizzaCebollaPanceta = ArticuloManufacturado.builder()
                    .denominacion("Pizza de cebolla y panceta")
                    .descripcion("Este producto es un arte culinario. Exquisito! Acá en el Buen Sabor solo ofrecemos lo mejor de lo mejor, solo para vos. Matate el hambre, dejate el buen sabor de boca.").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).resumen("Salsa de tomate, queso cheddar, cebolla en aros y panceta en tiras.")
                    .unidadMedida(unidadMedidaUnidad).precioVenta(3000.0).tiempoEstimadoMinutos(20)
                    .preparacion("Precalentar el horno a 220°C.\n" +
                            "Estirar la masa de pizza y colocarla en una bandeja para hornear.\n" +
                            "Distribuir la salsa de tomate sobre la masa, dejando un borde de 1 cm sin cubrir.\n" +
                            "Agregar el queso mozzarella rallado sobre la salsa de tomate.\n" +
                            "Distribuir los aros de cebolla y las tiras de panceta sobre el queso.\n" +
                            "Hornear la pizza durante 12-15 minutos, o hasta que el queso esté dorado y la masa esté crujiente.")
                    .categoria(categoriaPizzas).build();
            PizzaCebollaPanceta.getImagenes().add(ImagenArticulo.builder()
                    .url("https://github.com/Sebass24/El-Buen-Sabor-Frontend/blob/main/ElbuenSaborReactV1/public/Images/pizza-bacon-onion.jpg?raw=true")
                    .articulo(PizzaCebollaPanceta).build());
            PizzaCebollaPanceta.setArticuloManufacturadoDetalles(Set.of(
                    ArticuloManufacturadoDetalle.builder().cantidad(250d).articuloInsumo(articuloInsumoRepository.getById(37L)).articuloManufacturado(PizzaCebollaPanceta).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(150d).articuloInsumo(articuloInsumoRepository.getById(9L)).articuloManufacturado(PizzaCebollaPanceta).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(5d).articuloInsumo(articuloInsumoRepository.getById(22L)).articuloManufacturado(PizzaCebollaPanceta).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(5d).articuloInsumo(articuloInsumoRepository.getById(17L)).articuloManufacturado(PizzaCebollaPanceta).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(15d).articuloInsumo(articuloInsumoRepository.getById(28L)).articuloManufacturado(PizzaCebollaPanceta).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(100d).articuloInsumo(articuloInsumoRepository.getById(21L)).articuloManufacturado(PizzaCebollaPanceta).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(150d).articuloInsumo(articuloInsumoRepository.getById(39L)).articuloManufacturado(PizzaCebollaPanceta).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(50d).articuloInsumo(articuloInsumoRepository.getById(5L)).articuloManufacturado(PizzaCebollaPanceta).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(50d).articuloInsumo(articuloInsumoRepository.getById(2L)).articuloManufacturado(PizzaCebollaPanceta).build()
            ));

            // Crear Articulos Manufacturados PizzaChampinonesCebollaCaramelizada

            ArticuloManufacturado PizzaChampinonesCebollaCaramelizada = ArticuloManufacturado.builder()
                    .denominacion("Pizza de champiñones y cebolla caramelizada")
                    .descripcion("Este producto es un arte culinario. Exquisito! Acá en el Buen Sabor solo ofrecemos lo mejor de lo mejor, solo para vos. Matate el hambre, dejate el buen sabor de boca.").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).resumen("Salsa de tomate, champiñones, cebolla caramelizada, queso mozzarella y parmesano rallado.")
                    .unidadMedida(unidadMedidaUnidad).precioVenta(3000.0).tiempoEstimadoMinutos(20)
                    .preparacion("Precalentar el horno a 220°C.\n" +
                            "En una sartén grande, calentar el aceite de oliva a fuego medio-alto. Agregar los champiñones y saltear durante unos 5 minutos, hasta que estén dorados. Retirar de la sartén y reservar.\n" +
                            "Agregar las cebollas a la misma sartén y cocinar a fuego medio-bajo durante unos 15 minutos, hasta que estén caramelizadas. Agregar la cucharadita de azúcar a la sartén y mezclar bien.\n" +
                            "Estirar la bola de masa de pizza sobre una bandeja para hornear. Extender la salsa de tomate sobre la masa.\n" +
                            "Distribuir los champiñones salteados sobre la salsa de tomate. Luego, esparcir las cebollas caramelizadas sobre los champiñones.\n" +
                            "Espolvorear los quesos rallados sobre la parte superior de la pizza.\n" +
                            "Hornear durante unos 12-15 minutos, o hasta que la corteza esté dorada y crujiente.")
                    .categoria(categoriaPizzas).build();
            PizzaChampinonesCebollaCaramelizada.getImagenes().add(ImagenArticulo.builder()
                    .url("https://github.com/Sebass24/El-Buen-Sabor-Frontend/blob/main/ElbuenSaborReactV1/public/Images/pizza-mushrooms-onion.jpg?raw=true")
                    .articulo(PizzaChampinonesCebollaCaramelizada).build());
            PizzaChampinonesCebollaCaramelizada.setArticuloManufacturadoDetalles(Set.of(
                    ArticuloManufacturadoDetalle.builder().cantidad(250d).articuloInsumo(articuloInsumoRepository.getById(37L)).articuloManufacturado(PizzaChampinonesCebollaCaramelizada).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(150d).articuloInsumo(articuloInsumoRepository.getById(9L)).articuloManufacturado(PizzaChampinonesCebollaCaramelizada).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(5d).articuloInsumo(articuloInsumoRepository.getById(22L)).articuloManufacturado(PizzaChampinonesCebollaCaramelizada).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(5d).articuloInsumo(articuloInsumoRepository.getById(17L)).articuloManufacturado(PizzaChampinonesCebollaCaramelizada).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(15d).articuloInsumo(articuloInsumoRepository.getById(28L)).articuloManufacturado(PizzaChampinonesCebollaCaramelizada).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(100d).articuloInsumo(articuloInsumoRepository.getById(21L)).articuloManufacturado(PizzaChampinonesCebollaCaramelizada).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(70d).articuloInsumo(articuloInsumoRepository.getById(6L)).articuloManufacturado(PizzaChampinonesCebollaCaramelizada).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(150d).articuloInsumo(articuloInsumoRepository.getById(8L)).articuloManufacturado(PizzaChampinonesCebollaCaramelizada).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(100d).articuloInsumo(articuloInsumoRepository.getById(39L)).articuloManufacturado(PizzaChampinonesCebollaCaramelizada).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(25d).articuloInsumo(articuloInsumoRepository.getById(16L)).articuloManufacturado(PizzaChampinonesCebollaCaramelizada).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(30d).articuloInsumo(articuloInsumoRepository.getById(28L)).articuloManufacturado(PizzaChampinonesCebollaCaramelizada).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(4d).articuloInsumo(articuloInsumoRepository.getById(23L)).articuloManufacturado(PizzaChampinonesCebollaCaramelizada).build()
            ));
            // Crear Articulos Manufacturados PizzaPepperoniAceitunas

            ArticuloManufacturado PizzaPepperoniAceitunas = ArticuloManufacturado.builder()
                    .denominacion("Pizza de pepperoni y aceitunas")
                    .descripcion("Este producto es un arte culinario. Exquisito! Acá en el Buen Sabor solo ofrecemos lo mejor de lo mejor, solo para vos. Matate el hambre, dejate el buen sabor de boca.").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).resumen("Salsa de tomate, queso mozzarella, pepperoni, aceitunas verdes y queso parmesano.")
                    .unidadMedida(unidadMedidaUnidad).precioVenta(3200.0).tiempoEstimadoMinutos(20)
                    .preparacion("Precalentar el horno a 220°C.\n" +
                            "En una bandeja para hornear, estirar la bola de masa de pizza.\n" +
                            "Extender la salsa de tomate sobre la masa.\n" +
                            "Espolvorear el queso mozzarella rallado sobre la salsa de tomate.\n" +
                            "Distribuir las rodajas de pepperoni y las aceitunas sobre el queso.\n" +
                            "Espolvorear el queso parmesano rallado, el ajo en polvo, el orégano, la sal y la pimienta sobre la pizza.\n" +
                            "Hornear durante unos 12-15 minutos, o hasta que la corteza esté dorada y crujiente.")
                    .categoria(categoriaPizzas).build();
            PizzaPepperoniAceitunas.getImagenes().add(ImagenArticulo.builder()
                    .url("https://github.com/Sebass24/El-Buen-Sabor-Frontend/blob/main/ElbuenSaborReactV1/public/Images/pizza-pepperoni-olives.jpg?raw=true")
                    .articulo(PizzaPepperoniAceitunas).build());
            PizzaPepperoniAceitunas.setArticuloManufacturadoDetalles(Set.of(
                    ArticuloManufacturadoDetalle.builder().cantidad(250d).articuloInsumo(articuloInsumoRepository.getById(37L)).articuloManufacturado(PizzaPepperoniAceitunas).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(150d).articuloInsumo(articuloInsumoRepository.getById(9L)).articuloManufacturado(PizzaPepperoniAceitunas).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(5d).articuloInsumo(articuloInsumoRepository.getById(22L)).articuloManufacturado(PizzaPepperoniAceitunas).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(5d).articuloInsumo(articuloInsumoRepository.getById(17L)).articuloManufacturado(PizzaPepperoniAceitunas).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(15d).articuloInsumo(articuloInsumoRepository.getById(28L)).articuloManufacturado(PizzaPepperoniAceitunas).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(100d).articuloInsumo(articuloInsumoRepository.getById(21L)).articuloManufacturado(PizzaPepperoniAceitunas).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(100d).articuloInsumo(articuloInsumoRepository.getById(39L)).articuloManufacturado(PizzaPepperoniAceitunas).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(25d).articuloInsumo(articuloInsumoRepository.getById(16L)).articuloManufacturado(PizzaPepperoniAceitunas).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(35d).articuloInsumo(articuloInsumoRepository.getById(12L)).articuloManufacturado(PizzaPepperoniAceitunas).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(30d).articuloInsumo(articuloInsumoRepository.getById(3L)).articuloManufacturado(PizzaPepperoniAceitunas).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(0.5d).articuloInsumo(articuloInsumoRepository.getById(22L)).articuloManufacturado(PizzaPepperoniAceitunas).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(0.5d).articuloInsumo(articuloInsumoRepository.getById(26L)).articuloManufacturado(PizzaPepperoniAceitunas).build()
            ));

            // Crear Articulos Manufacturados PapasFritasClasicasChicas

            ArticuloManufacturado PapasFritasClasicasChicas = ArticuloManufacturado.builder()
                    .denominacion("Papas fritas clásicas chicas")
                    .descripcion("Este producto es un arte culinario. Exquisito! Acá en el Buen Sabor solo ofrecemos lo mejor de lo mejor, solo para vos. Matate el hambre, dejate el buen sabor de boca.").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).resumen("150gr de papas frescas cortadas a mano, sin sal.")
                    .unidadMedida(unidadMedidaUnidad).precioVenta(1500.0).tiempoEstimadoMinutos(10)
                    .preparacion("(Pelar y cortar las papas en tiras delgadas.\n" +
                            "En una sartén profunda o una freidora, calentar el aceite a 180°C.\n" +
                            "Freír las papas por unos 5-7 minutos o hasta que estén doradas.\n" +
                            "Retirar las papas del aceite y colocarlas en un plato con papel absorbente para eliminar el exceso de aceite.\"),\n")
                    .categoria(categoriaPapas).build();
            PapasFritasClasicasChicas.getImagenes().add(ImagenArticulo.builder()
                    .url("https://github.com/Sebass24/El-Buen-Sabor-Frontend/blob/main/ElbuenSaborReactV1/public/Images/fries.jpg?raw=true")
                    .articulo(PapasFritasClasicasChicas).build());
            PapasFritasClasicasChicas.setArticuloManufacturadoDetalles(Set.of(
                    ArticuloManufacturadoDetalle.builder().cantidad(150d).articuloInsumo(articuloInsumoRepository.getById(10L)).articuloManufacturado(PapasFritasClasicasChicas).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(100d).articuloInsumo(articuloInsumoRepository.getById(29L)).articuloManufacturado(PapasFritasClasicasChicas).build()));

            // Crear Articulos Manufacturados PapasFritasClasicasMedianas

            ArticuloManufacturado PapasFritasClasicasMedianas = ArticuloManufacturado.builder()
                    .denominacion("Papas fritas clásicas medianas")
                    .descripcion("Este producto es un arte culinario. Exquisito! Acá en el Buen Sabor solo ofrecemos lo mejor de lo mejor, solo para vos. Matate el hambre, dejate el buen sabor de boca.").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).resumen("300gr de papas frescas cortadas a mano, sin sal.")
                    .unidadMedida(unidadMedidaUnidad).precioVenta(2000.0).tiempoEstimadoMinutos(10)
                    .preparacion("Pelar y cortar las papas en tiras delgadas.\n" +
                            "En una sartén profunda o una freidora, calentar el aceite a 180°C.\n" +
                            "Freír las papas por unos 5-7 minutos o hasta que estén doradas.\n" +
                            "Retirar las papas del aceite y colocarlas en un plato con papel absorbente para eliminar el exceso de aceite.\"),\n")
                    .categoria(categoriaPapas).build();
            PapasFritasClasicasMedianas.getImagenes().add(ImagenArticulo.builder()
                    .url("https://github.com/Sebass24/El-Buen-Sabor-Frontend/blob/main/ElbuenSaborReactV1/public/Images/fries.jpg?raw=true")
                    .articulo(PapasFritasClasicasMedianas).build());
            PapasFritasClasicasMedianas.setArticuloManufacturadoDetalles(Set.of(
                    ArticuloManufacturadoDetalle.builder().cantidad(300d).articuloInsumo(articuloInsumoRepository.getById(10L)).articuloManufacturado(PapasFritasClasicasMedianas).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(100d).articuloInsumo(articuloInsumoRepository.getById(29L)).articuloManufacturado(PapasFritasClasicasMedianas).build()));

            // Crear Articulos Manufacturados PapasFritasClasicasGrandes

            ArticuloManufacturado PapasFritasClasicasGrandes = ArticuloManufacturado.builder()
                    .denominacion("Papas fritas clásicas grandes")
                    .descripcion("Este producto es un arte culinario. Exquisito! Acá en el Buen Sabor solo ofrecemos lo mejor de lo mejor, solo para vos. Matate el hambre, dejate el buen sabor de boca.").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).resumen("450gr de papas frescas cortadas a mano, sin sal.")
                    .unidadMedida(unidadMedidaUnidad).precioVenta(2500.0).tiempoEstimadoMinutos(10)
                    .preparacion("Pelar y cortar las papas en tiras delgadas.\n" +
                            "En una sartén profunda o una freidora, calentar el aceite a 180°C.\n" +
                            "Freír las papas por unos 5-7 minutos o hasta que estén doradas.\n" +
                            "Retirar las papas del aceite y colocarlas en un plato con papel absorbente para eliminar el exceso de aceite.")
                    .categoria(categoriaPapas).build();
            PapasFritasClasicasGrandes.getImagenes().add(ImagenArticulo.builder()
                    .url("https://github.com/Sebass24/El-Buen-Sabor-Frontend/blob/main/ElbuenSaborReactV1/public/Images/fries.jpg?raw=true")
                    .articulo(PapasFritasClasicasGrandes).build());
            PapasFritasClasicasGrandes.setArticuloManufacturadoDetalles(Set.of(
                    ArticuloManufacturadoDetalle.builder().cantidad(450d).articuloInsumo(articuloInsumoRepository.getById(10L)).articuloManufacturado(PapasFritasClasicasGrandes).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(100d).articuloInsumo(articuloInsumoRepository.getById(29L)).articuloManufacturado(PapasFritasClasicasGrandes).build()));


            // Crear Articulos Manufacturados PapasFritasQuesoPanceta

            ArticuloManufacturado PapasFritasQuesoPanceta = ArticuloManufacturado.builder()
                    .denominacion("Papas fritas con queso y panceta")
                    .descripcion("Este producto es un arte culinario. Exquisito! Acá en el Buen Sabor solo ofrecemos lo mejor de lo mejor, solo para vos. Matate el hambre, dejate el buen sabor de boca.").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).resumen("Papas frescas cortadas a mano, panceta y queso cheddar.")
                    .unidadMedida(unidadMedidaUnidad).precioVenta(3000.0).tiempoEstimadoMinutos(15)
                    .preparacion("Pelar y cortar las papas en tiras delgadas.\n" +
                            "En una sartén profunda o una freidora, calentar el aceite a 180°C.\n" +
                            "Freír las papas por unos 5-7 minutos o hasta que estén doradas.\n" +
                            "Retirar las papas del aceite y colocarlas en un plato con papel absorbente para eliminar el exceso de aceite.\n" +
                            "Espolvorear las papas con el queso rallado y la panceta desmenuzada.")
                    .categoria(categoriaPapas).build();
            PapasFritasQuesoPanceta.getImagenes().add(ImagenArticulo.builder()
                    .url("https://github.com/Sebass24/El-Buen-Sabor-Frontend/blob/main/ElbuenSaborReactV1/public/Images/fries-bacon-cheese.jpg?raw=true")
                    .articulo(PapasFritasQuesoPanceta).build());
            PapasFritasQuesoPanceta.setArticuloManufacturadoDetalles(Set.of(
                    ArticuloManufacturadoDetalle.builder().cantidad(300d).articuloInsumo(articuloInsumoRepository.getById(10L)).articuloManufacturado(PapasFritasQuesoPanceta).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(100d).articuloInsumo(articuloInsumoRepository.getById(29L)).articuloManufacturado(PapasFritasQuesoPanceta).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(30d).articuloInsumo(articuloInsumoRepository.getById(2L)).articuloManufacturado(PapasFritasQuesoPanceta).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(35d).articuloInsumo(articuloInsumoRepository.getById(15L)).articuloManufacturado(PapasFritasQuesoPanceta).build()));


            // Crear Articulos Manufacturados PapasFritasAjoParmesano
            ArticuloManufacturado PapasFritasAjoParmesano = ArticuloManufacturado.builder()
                    .denominacion("Papas fritas con ajo y parmesano")
                    .descripcion("Este producto es un arte culinario. Exquisito! Acá en el Buen Sabor solo ofrecemos lo mejor de lo mejor, solo para vos. Matate el hambre, dejate el buen sabor de boca.").sucursales(Set.of(sucursalChacras, sucursalGodoyCruz)).resumen("Papas frescas cortadas a mano, ajo picado y queso parmesano.")
                    .unidadMedida(unidadMedidaUnidad).precioVenta(3500.0).tiempoEstimadoMinutos(15)
                    .preparacion("Pelar y cortar las papas en tiras delgadas.\n" +
                            "En una sartén profunda o una freidora, calentar el aceite a 180°C.\n" +
                            "Freír las papas por unos 5-7 minutos o hasta que estén doradas.\n" +
                            "Retirar las papas del aceite y colocarlas en un plato con papel absorbente para eliminar el exceso de aceite.\n" +
                            "Mezclar el ajo picado con el queso parmesano rallado.\n" +
                            "Espolvorear las papas con la mezcla de ajo y queso.")
                    .categoria(categoriaPapas).build();
            PapasFritasAjoParmesano.getImagenes().add(ImagenArticulo.builder()
                    .url("https://github.com/Sebass24/El-Buen-Sabor-Frontend/blob/main/ElbuenSaborReactV1/public/Images/fries-parmesan-garlic.jpg?raw=true")
                    .articulo(PapasFritasAjoParmesano).build());
            PapasFritasAjoParmesano.setArticuloManufacturadoDetalles(Set.of(
                    ArticuloManufacturadoDetalle.builder().cantidad(300d).articuloInsumo(articuloInsumoRepository.getById(10L)).articuloManufacturado(PapasFritasAjoParmesano).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(100d).articuloInsumo(articuloInsumoRepository.getById(29L)).articuloManufacturado(PapasFritasAjoParmesano).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(25d).articuloInsumo(articuloInsumoRepository.getById(16L)).articuloManufacturado(PapasFritasAjoParmesano).build(),
                    ArticuloManufacturadoDetalle.builder().cantidad(6d).articuloInsumo(articuloInsumoRepository.getById(11L)).articuloManufacturado(PapasFritasAjoParmesano).build()));


            // Crear promocion para sucursal - Dia de los enamorados
            // Tener en cuenta que esa promocion es exclusivamente para una sucursal determinada d euna empresa determinada
            Promocion promocionDiaEnamorados = Promocion.builder().denominacion("Dia de los Enamorados")
                    .fechaDesde(LocalDate.of(2024, 2, 13))
                    .fechaHasta(LocalDate.of(2024, 2, 15))
                    .horaDesde(LocalTime.of(0, 0))
                    .horaHasta(LocalTime.of(23, 59))
                    .descripcionDescuento("14 de febrero es el día de los enamorados")
                    .precioPromocional(4000d)
                    .sucursales(Set.of(sucursalChacras, sucursalGodoyCruz))
                    .tipoPromocion(TipoPromocion.Promocion)
                    .build();
            PromocionDetalle promocionDetalleEnamoradosCocaCola = PromocionDetalle.builder().articulo(cocaCola).cantidad(1).promocion(promocionDiaEnamorados).build();
            PromocionDetalle promocionDetalleEnamoradosNapolitana = PromocionDetalle.builder().articulo(pizzaNapolitana).cantidad(1).promocion(promocionDiaEnamorados).build();
            promocionDiaEnamorados.getPromocionDetalles().add(promocionDetalleEnamoradosCocaCola);
            promocionDiaEnamorados.getPromocionDetalles().add(promocionDetalleEnamoradosNapolitana);
            ImagenArticulo imagenPromocionEnamorados = ImagenArticulo.builder().url("https://www.bbva.com/wp-content/uploads/2021/02/san-valentin-14-febrero-corazon-amor-bbva-recurso-1920x1280-min.jpg").articulo(promocionDiaEnamorados).build();
            promocionDiaEnamorados.getImagenes().add(imagenPromocionEnamorados);
            promocionDiaEnamorados.setPrecioPromocional(promocionDiaEnamorados.getPromocionDetalles().stream().mapToDouble(detalle -> detalle.getArticulo().getPrecioVenta()).sum() * 0.9);


            // Crear promoción para sucursal - Día del Amigo
            Promocion promocionDiaAmigo = Promocion.builder()
                    .denominacion("Día del Amigo")
                    .fechaDesde(LocalDate.of(2024, 7, 19))
                    .fechaHasta(LocalDate.of(2024, 7, 21))
                    .horaDesde(LocalTime.of(0, 0))
                    .horaHasta(LocalTime.of(23, 59))
                    .descripcionDescuento("Celebra el Día del Amigo con nosotros con estas ofertas especiales")
                    .precioPromocional(3500d)
                    .sucursales(Set.of(sucursalChacras, sucursalGodoyCruz))
                    .tipoPromocion(TipoPromocion.Promocion)
                    .build();

            PromocionDetalle promocionDetalleAmigoHamburguesaClasica = PromocionDetalle.builder()
                    .articulo(HamburguesaClasica)
                    .cantidad(1)
                    .promocion(promocionDiaAmigo)
                    .build();

            PromocionDetalle promocionDetalleAmigoPapasFritasQuesoPanceta = PromocionDetalle.builder()
                    .articulo(PapasFritasQuesoPanceta)
                    .cantidad(1)
                    .promocion(promocionDiaAmigo)
                    .build();

            promocionDiaAmigo.getPromocionDetalles().add(promocionDetalleAmigoHamburguesaClasica);
            promocionDiaAmigo.getPromocionDetalles().add(promocionDetalleAmigoPapasFritasQuesoPanceta);

            ImagenArticulo imagenPromocionAmigo = ImagenArticulo.builder()
                    .url("https://media.istockphoto.com/id/1473452859/es/foto/sabrosa-hamburguesa-con-queso-vaso-de-cola-y-papas-fritas-en-primer-plano-de-bandeja-de-madera.webp?b=1&s=170667a&w=0&k=20&c=So3L-lvRKzFcrSTPjkKIauH8x59FNhcZDGn8iQJOqaU=")
                    .articulo(promocionDiaAmigo)
                    .build();

            promocionDiaAmigo.getImagenes().add(imagenPromocionAmigo);
            promocionDiaAmigo.setPrecioPromocional(promocionDiaAmigo.getPromocionDetalles().stream().mapToDouble(detalle -> detalle.getArticulo().getPrecioVenta()).sum() * 0.9);


            // Crear promoción para sucursal - Fin de Semana de Pizza
            Promocion promocionFinSemanaPizza = Promocion.builder()
                    .denominacion("Fin de Semana de Pizza")
                    .fechaDesde(LocalDate.of(2024, 6, 28))
                    .fechaHasta(LocalDate.of(2024, 6, 30))
                    .horaDesde(LocalTime.of(0, 0))
                    .horaHasta(LocalTime.of(23, 59))
                    .descripcionDescuento("Disfruta de un fin de semana con las mejores pizzas a precios especiales")
                    .precioPromocional(4000d)
                    .sucursales(Set.of(sucursalChacras, sucursalGodoyCruz))
                    .tipoPromocion(TipoPromocion.Promocion)
                    .build();

            PromocionDetalle promocionDetallePizzaNapolitana = PromocionDetalle.builder()
                    .articulo(pizzaNapolitana)
                    .cantidad(1)
                    .promocion(promocionFinSemanaPizza)
                    .build();

            PromocionDetalle promocionDetallePizzaPepperoniAceitunas = PromocionDetalle.builder()
                    .articulo(PizzaPepperoniAceitunas)
                    .cantidad(1)
                    .promocion(promocionFinSemanaPizza)
                    .build();

            promocionFinSemanaPizza.getPromocionDetalles().add(promocionDetallePizzaNapolitana);
            promocionFinSemanaPizza.getPromocionDetalles().add(promocionDetallePizzaPepperoniAceitunas);

            ImagenArticulo imagenPromocionFinSemanaPizza = ImagenArticulo.builder()
                    .url("https://www.pizzadelchef.com/wp-content/uploads/2022/09/especialidades-2x1-1-300x300.jpg")
                    .articulo(promocionFinSemanaPizza)
                    .build();

            promocionFinSemanaPizza.getImagenes().add(imagenPromocionFinSemanaPizza);
            promocionFinSemanaPizza.setPrecioPromocional(promocionFinSemanaPizza.getPromocionDetalles().stream().mapToDouble(detalle -> detalle.getArticulo().getPrecioVenta()).sum() * 0.9);


            sucursalChacras.setPromociones(Set.of(promocionDiaEnamorados, promocionDiaAmigo, promocionFinSemanaPizza));
            sucursalGodoyCruz.setPromociones(Set.of(promocionDiaEnamorados, promocionDiaAmigo, promocionFinSemanaPizza));

            articuloInsumoRepository.save(cocaCola);
            articuloInsumoRepository.save(harina);
            articuloInsumoRepository.save(tomate);
            articuloInsumoRepository.save(queso);
            articuloManufacturadoRepository.save(HamburguesaClasica);
            articuloManufacturadoRepository.save(HamburguesaChampinones);
            articuloManufacturadoRepository.save(HamburguesaPicante);
            articuloManufacturadoRepository.save(HamburguesaChedarChampinones);
            articuloManufacturadoRepository.save(HamburguesaPancetaHuevo);
            articuloManufacturadoRepository.save(pizzaMuzarella);
            articuloManufacturadoRepository.save(pizzaNapolitana);
            articuloManufacturadoRepository.save(PizzaPepperoniChampinones);
            articuloManufacturadoRepository.save(PizzaCebollaPanceta);
            articuloManufacturadoRepository.save(PizzaChampinonesCebollaCaramelizada);
            articuloManufacturadoRepository.save(PizzaPepperoniAceitunas);
            articuloManufacturadoRepository.save(PizzaCarneMolidaPimientos);
            articuloManufacturadoRepository.save(PapasFritasClasicasChicas);
            articuloManufacturadoRepository.save(PapasFritasClasicasMedianas);
            articuloManufacturadoRepository.save(PapasFritasClasicasGrandes);
            articuloManufacturadoRepository.save(PapasFritasQuesoPanceta);
            articuloManufacturadoRepository.save(PapasFritasAjoParmesano);

            promocionRepository.saveAll(Set.of(promocionDiaEnamorados, promocionDiaAmigo, promocionFinSemanaPizza));
            sucursalRepository.saveAll(Set.of(sucursalChacras, sucursalGodoyCruz));

            // AGREGAR CLIENTE
            UsuarioCliente usuario1 = UsuarioCliente.builder().username("pepe-honguito75").auth0Id("6656512773ce20dd72a2f698").build();
            ImagenCliente imagenCliente = ImagenCliente.builder().url("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQsa2xSPPay4GD7E3cthBMCcvPMADEjFufUWQ&s").build();
            Domicilio domicilioCliente1 = Domicilio.builder().calle("Sarmiento").numero(123).cp(5507).localidad(localidad1).build();
            Domicilio domicilioCliente2 = Domicilio.builder().calle("San martin").numero(412).cp(5501).localidad(localidad2).build();
            Cliente cliente1 = Cliente.builder().nombre("Alejandro").eliminado(false).email("alex@gmail.com").apellido("Lencinas").dni("35515440").cuil("20355154409").imagen(imagenCliente).telefono("2634666666").rol(Rol.Cliente).usuario(usuario1).fechaNacimiento(LocalDate.of(1990, 12, 15)).build();
            cliente1.getDomicilios().add(domicilioCliente1);
            cliente1.getDomicilios().add(domicilioCliente2);
            clienteRepository.save(cliente1);
            logger.info("Cliente {}", cliente1);

            // AGREGAR CLIENTE
            UsuarioCliente usuarioCliente2 = UsuarioCliente.builder().username("pepe-honguito75").auth0Id("iVBORw0KGgoAAAANSUhEUgAAAK0AAACUCAMAAADWBFkUAAABEVBMVEX").build();

            ImagenCliente imagenCliente2 = ImagenCliente.builder().url("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQsa2xSPPay4GD7E3cthBMCcvPMADEjFufUWQ&s").build();
            Domicilio cliente2domicilio1 = Domicilio.builder().calle("Sarmiento").numero(123).cp(5507).localidad(localidad1).build();
            Domicilio cliente2domicilio2 = Domicilio.builder().calle("San martin").numero(412).cp(5501).localidad(localidad2).build();
            Cliente cliente2 = Cliente.builder().nombre("Pepito").eliminado(false).email("pepitocomilon@gmail.com").apellido("Comilon").dni("35515000").cuil("20355150009").imagen(imagenCliente2).telefono("2634666123").rol(Rol.Cliente).usuario(usuarioCliente2).fechaNacimiento(LocalDate.of(1990, 12, 15)).build();
            cliente2.getDomicilios().add(cliente2domicilio1);
            cliente2.getDomicilios().add(cliente2domicilio2);
            clienteRepository.save(cliente2);
            logger.info("Cliente {}", cliente2);

            UsuarioCliente usuarioCliente3 = UsuarioCliente.builder().username("pepe-honguito75").auth0Id("iVBORw0KGgoAAAANSUhEUgAAAK0AAACUCAMAAADWBFkUAAABEVBMVEX").build();
            ImagenCliente imagenCliente3 = ImagenCliente.builder().url("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQsa2xSPPay4GD7E3cthBMCcvPMADEjFufUWQ&s").build();
            Domicilio cliente3domicilio1 = Domicilio.builder().calle("Belgrano").numero(223).cp(5507).localidad(localidad1).build();
            Domicilio cliente3domicilio2 = Domicilio.builder().calle("San Juan").numero(612).cp(5501).localidad(localidad2).build();
            Cliente cliente3 = Cliente.builder().nombre("Jorgito").eliminado(false).email("pepitocomilon@gmail.com").apellido("Comilon").dni("35515111").cuil("20355151119").imagen(imagenCliente3).telefono("2634645123").rol(Rol.Cliente).usuario(usuarioCliente3).fechaNacimiento(LocalDate.of(1990, 12, 15)).build();
            cliente3.getDomicilios().add(cliente3domicilio1);
            cliente3.getDomicilios().add(cliente3domicilio2);
            clienteRepository.save(cliente3);
            logger.info("Cliente {}", cliente3);

            UsuarioCliente usuarioCliente4 = UsuarioCliente.builder().username("pepe-honguito75").auth0Id("iVBORw0KGgoAAAANSUhEUgAAAK0AAACUCAMAAADWBFkUAAABEVBMVEX").build();

            ImagenCliente imagenCliente4 = ImagenCliente.builder().url("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQsa2xSPPay4GD7E3cthBMCcvPMADEjFufUWQ&s").build();
            Domicilio cliente4domicilio1 = Domicilio.builder().calle("Rioja").numero(56).cp(5507).localidad(localidad1).build();
            Domicilio cliente4domicilio2 = Domicilio.builder().calle("San Juan").numero(625).cp(5501).localidad(localidad2).build();
            Cliente cliente4 = Cliente.builder().nombre("Bernardito").eliminado(false).email("pepitocomilon@gmail.com").apellido("Comilon").dni("35515222").cuil("20355152229").imagen(imagenCliente4).telefono("2634545123").rol(Rol.Cliente).usuario(usuarioCliente4).fechaNacimiento(LocalDate.of(1990, 12, 15)).build();
            cliente4.getDomicilios().add(cliente4domicilio1);
            cliente4.getDomicilios().add(cliente4domicilio2);
            clienteRepository.save(cliente4);
            logger.info("Cliente {}", cliente4);

            UsuarioCliente usuarioCliente5 = UsuarioCliente.builder().username("pepe-honguito75").auth0Id("iVBORw0KGgoAAAANSUhEUgAAAK0AAACUCAMAAADWBFkUAAABEVBMVEX").build();

            ImagenCliente imagenCliente5 = ImagenCliente.builder().url("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQsa2xSPPay4GD7E3cthBMCcvPMADEjFufUWQ&s").build();
            Domicilio cliente5domicilio1 = Domicilio.builder().calle("Corrientes").numero(223).cp(5507).localidad(localidad1).build();
            Domicilio cliente5domicilio2 = Domicilio.builder().calle("San Martin").numero(612).cp(5501).localidad(localidad2).build();
            Cliente cliente5 = Cliente.builder().nombre("Samuelito").eliminado(false).email("pepitocomilon@gmail.com").apellido("Comilon").dni("35515333").cuil("20355153339").imagen(imagenCliente5).telefono("2634691123").rol(Rol.Cliente).usuario(usuarioCliente5).fechaNacimiento(LocalDate.of(1990, 12, 15)).build();
            cliente5.getDomicilios().add(cliente5domicilio1);
            cliente5.getDomicilios().add(cliente5domicilio2);
            clienteRepository.save(cliente5);
            logger.info("Cliente {}", cliente5);

            UsuarioCliente usuarioCliente6 = UsuarioCliente.builder().username("pepe-honguito75").auth0Id("iVBORw0KGgoAAAANSUhEUgAAAK0AAACUCAMAAADWBFkUAAABEVBMVEX").build();

            ImagenCliente imagenCliente6 = ImagenCliente.builder().url("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQsa2xSPPay4GD7E3cthBMCcvPMADEjFufUWQ&s").build();
            Domicilio cliente6domicilio1 = Domicilio.builder().calle("Corrientes").numero(103).cp(5507).localidad(localidad1).build();
            Domicilio cliente6domicilio2 = Domicilio.builder().calle("Colon").numero(612).cp(5501).localidad(localidad2).build();
            Cliente cliente6 = Cliente.builder().nombre("Morcillito").eliminado(false).email("pepitocomilon@gmail.com").apellido("Comilon").dni("35515444").cuil("20355154449").imagen(imagenCliente6).telefono("2634676323").dni("42219220").cuil("20422192209").rol(Rol.Cliente).usuario(usuarioCliente6).fechaNacimiento(LocalDate.of(1990, 12, 15)).build();
            cliente6.getDomicilios().add(cliente6domicilio1);
            cliente6.getDomicilios().add(cliente6domicilio2);
            clienteRepository.save(cliente6);
            logger.info("Cliente {}", cliente6);

            //EMPLEADOS
            UsuarioEmpleado usuarioAdmin = UsuarioEmpleado.builder().username("juancitoAdmin").auth0Id("6658f33db1fb2d3be86e7fee").build();
            UsuarioEmpleado usuarioCajero = UsuarioEmpleado.builder().username("juancitoCajero").auth0Id("6658f4d3bdc93dff066b0651").build();
            UsuarioEmpleado usuarioCocinero = UsuarioEmpleado.builder().username("pepitoCocinero").auth0Id("6658f3968b8138d6b49e006c").build();
            UsuarioEmpleado usuarioDelivery = UsuarioEmpleado.builder().username("elJorgeDeliverys").auth0Id("6658f3c1b8a7a2b0d0f8ddec").build();

            //Agregar Empleados 4
            ImagenEmpleado imagenEmpleadoAdmin = ImagenEmpleado.builder().url("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQsa2xSPPay4GD7E3cthBMCcvPMADEjFufUWQ&s").build();
            Domicilio domicilioEmpleadoAdmin = Domicilio.builder().calle("Sarmiento").numero(123).cp(5507).localidad(localidad1).build();
            Empleado empleadoAdmin = Empleado.builder().nombre("Juancito").sucursal(sucursalChacras).rol(Rol.Administrador).domicilio(domicilioEmpleadoAdmin).email("juancitoadmin@gmail.com").apellido("Admincias").imagen(imagenEmpleadoAdmin).telefono("2634666266").usuario(usuarioAdmin).fechaNacimiento(LocalDate.of(1990, 11, 15)).build();
            empleadoRepository.save(empleadoAdmin);

            ImagenEmpleado imagenEmpleadoCajero = ImagenEmpleado.builder().url("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQsa2xSPPay4GD7E3cthBMCcvPMADEjFufUWQ&s").build();
            Domicilio domicilioEmpleadoCajero = Domicilio.builder().calle("Sarmiento").numero(123).cp(5507).localidad(localidad1).build();
            Empleado empleadoCajero = Empleado.builder().nombre("Juancito").sucursal(sucursalChacras).rol(Rol.Cajero).domicilio(domicilioEmpleadoCajero).email("juancitocajeres@gmail.com").apellido("Cajeres").imagen(imagenEmpleadoCajero).telefono("263443626").usuario(usuarioCajero).fechaNacimiento(LocalDate.of(1991, 8, 9)).build();

            ImagenEmpleado imagenEmpleadoCocinero = ImagenEmpleado.builder().url("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQsa2xSPPay4GD7E3cthBMCcvPMADEjFufUWQ&s").build();
            Domicilio domicilioEmpleadoCocinero = Domicilio.builder().calle("Sarmiento").numero(123).cp(5507).localidad(localidad1).build();
            Empleado empleadoCocinero = Empleado.builder().nombre("Pepito").sucursal(sucursalChacras).rol(Rol.Cocinero).domicilio(domicilioEmpleadoCocinero).email("pepitococinas@gmail.com").apellido("Cocinas").imagen(imagenEmpleadoCocinero).telefono("2634666166").usuario(usuarioCocinero).fechaNacimiento(LocalDate.of(1992, 6, 12)).build();

            ImagenEmpleado imagenEmpleadoDelivery = ImagenEmpleado.builder().url("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQsa2xSPPay4GD7E3cthBMCcvPMADEjFufUWQ&s").build();
            Domicilio domicilioEmpleadoDelivery = Domicilio.builder().calle("Sarmiento").numero(123).cp(5507).localidad(localidad1).build();
            Empleado empleadoDelivery = Empleado.builder().nombre("Jorge").sucursal(sucursalChacras).rol(Rol.Delivery).domicilio(domicilioEmpleadoDelivery).email("jorgedeliveres@gmail.com").apellido("Deliveres").imagen(imagenEmpleadoDelivery).telefono("2634636656").usuario(usuarioDelivery).fechaNacimiento(LocalDate.of(1993, 4, 11)).build();

            Set<Empleado> empleados = Set.of(empleadoCajero, empleadoCocinero, empleadoDelivery);

            empleados.forEach(empleado -> {

                for (Dia dia : Dia.values()) {
                    // Crear el conjunto de horarioDetalles para el día de la semana
                    HorarioEmpleado horarioEmpleado = HorarioEmpleado.builder()
                            .empleado(empleado)
                            .diaSemana(dia)
                            .build();
                    horarioEmpleado.getHorarioDetalles().add(HorarioDetalleEmpleado.builder()
                            .horaInicio(LocalTime.of(20, 0))
                            .horaFin(LocalTime.of(0, 0))
                            .horario(horarioEmpleado)
                            .build());
                    // Configurar el horario según el día de la semana
                    if (dia == Dia.Sabado || dia == Dia.Domingo) {
                        // Para sábados y domingos de 11:00 a 15:00
                        horarioEmpleado.getHorarioDetalles().add(HorarioDetalleEmpleado.builder()
                                .horaInicio(LocalTime.of(11, 0))
                                .horaFin(LocalTime.of(15, 0))
                                .horario(horarioEmpleado)
                                .build());
                    }

                    empleado.getHorarios().add(horarioEmpleado);
                }
            });

            empleadoRepository.saveAll(empleados);

            // agregar pedido

            Pedido pedido = Pedido.builder()
                    .domicilio(domicilioCliente1)
                    .estado(Estado.FACTURADO)
                    .formaPago(FormaPago.MercadoPago)
                    .fechaPedido(new Date(124, 0, 8))
                    .horaEstimadaFinalizacion(LocalTime.of(1, 2, 3))
                    .sucursal(sucursalChacras)
                    .tipoEnvio(TipoEnvio.Delivery)
                    .total(0d)
                    .totalCosto(2855d)
                    .cliente(cliente1)
                    .empleado(empleadoCajero)
                    .build();
            DetallePedido detallePedido1 = DetallePedido.builder().articulo(pizzaMuzarella).cantidad(1).subTotal(pizzaMuzarella.getPrecioVenta()).pedido(pedido).build();
            DetallePedido detallePedido2 = DetallePedido.builder().articulo(cocaCola).cantidad(1).subTotal(cocaCola.getPrecioVenta()).pedido(pedido).build();
            pedido.getDetallePedidos().add(detallePedido1);
            pedido.getDetallePedidos().add(detallePedido2);
            pedido.setTotal(pedido.getDetallePedidos().stream().mapToDouble(detalle -> detalle.getSubTotal()).sum());


            Factura factura = Factura.builder().fechaFacturacion(LocalDate.of(2024, 2, 13)).formaPago(FormaPago.MercadoPago).mpMerchantOrderId(1).mpPaymentId(1).mpPaymentType("mercado pago").mpPreferenceId("0001").totalVenta(2500d).pedido(pedido).build();

            pedido.setFactura(factura);

            pedidoRepository.save(pedido);

            // agregar pedido

            Pedido pedido2 = Pedido.builder()
                    .domicilio(domicilioCliente1)
                    .estado(Estado.FACTURADO)
                    .formaPago(FormaPago.MercadoPago)
                    .fechaPedido(new Date(124, 1, 1))
                    .horaEstimadaFinalizacion(LocalTime.of(1, 2, 3))
                    .sucursal(sucursalChacras)
                    .tipoEnvio(TipoEnvio.Delivery)
                    .total(0d)
                    .totalCosto(838d)
                    .cliente(cliente2)
                    .empleado(empleadoCajero)
                    .build();
            DetallePedido detallePedido3 = DetallePedido.builder().articulo(PapasFritasClasicasGrandes).cantidad(1).subTotal(PapasFritasClasicasGrandes.getPrecioVenta()).pedido(pedido2).build();
            DetallePedido detallePedido4 = DetallePedido.builder().articulo(HamburguesaPancetaHuevo).cantidad(1).subTotal(HamburguesaPancetaHuevo.getPrecioVenta()).pedido(pedido2).build();
            pedido2.getDetallePedidos().add(detallePedido3);
            pedido2.getDetallePedidos().add(detallePedido4);
            pedido2.setTotal(pedido2.getDetallePedidos().stream().mapToDouble(detalle -> detalle.getSubTotal()).sum());

            Factura factura2 = Factura.builder().fechaFacturacion(LocalDate.of(2024, 2, 13)).formaPago(FormaPago.MercadoPago).mpMerchantOrderId(1).mpPaymentId(1).mpPaymentType("mercado pago").mpPreferenceId("0001").totalVenta(2500d).pedido(pedido2).build();
            pedido2.setFactura(factura2);
            pedidoRepository.save(pedido2);

            // Creación del pedido 3
            Pedido pedido3 = Pedido.builder()
                    .domicilio(domicilioCliente1)
                    .estado(Estado.PENDIENTE)
                    .formaPago(FormaPago.MercadoPago)
                    .fechaPedido(new Date(124, 3, 2))
                    .horaEstimadaFinalizacion(LocalTime.of(1, 2, 3))
                    .sucursal(sucursalChacras)
                    .tipoEnvio(TipoEnvio.Delivery)
                    .total(0d)
                    .totalCosto(1593d)
                    .cliente(cliente3)
                    .empleado(empleadoCajero)
                    .build();
            DetallePedido detallePedido5 = DetallePedido.builder().articulo(HamburguesaChampinones).cantidad(1).subTotal(HamburguesaChampinones.getPrecioVenta()).pedido(pedido3).build();
            DetallePedido detallePedido6 = DetallePedido.builder().articulo(PizzaCarneMolidaPimientos).cantidad(1).subTotal(PizzaCarneMolidaPimientos.getPrecioVenta()).pedido(pedido3).build();
            pedido3.getDetallePedidos().add(detallePedido5);
            pedido3.getDetallePedidos().add(detallePedido6);
            pedido3.setTotal(pedido3.getDetallePedidos().stream().mapToDouble(detalle -> detalle.getSubTotal()).sum());

            Factura factura3 = Factura.builder().fechaFacturacion(LocalDate.of(2024, 2, 13)).formaPago(FormaPago.MercadoPago).mpMerchantOrderId(2).mpPaymentId(2).mpPaymentType("mercado pago").mpPreferenceId("0002").totalVenta(2500d).pedido(pedido3).build();
            pedido3.setFactura(factura3);
            pedidoRepository.save(pedido3);

            // Creación del pedido 4
            Pedido pedido4 = Pedido.builder()
                    .domicilio(domicilioCliente1)
                    .estado(Estado.TERMINADO)
                    .formaPago(FormaPago.Efectivo)
                    .fechaPedido(new Date(124, 3, 12))
                    .horaEstimadaFinalizacion(LocalTime.of(1, 2, 3))
                    .sucursal(sucursalChacras)
                    .tipoEnvio(TipoEnvio.TakeAway)
                    .total(0d)
                    .totalCosto(1640d)
                    .cliente(cliente4)
                    .empleado(empleadoCajero)
                    .build();
            DetallePedido detallePedido7 = DetallePedido.builder().articulo(PizzaPepperoniAceitunas).cantidad(1).subTotal(PizzaPepperoniAceitunas.getPrecioVenta()).pedido(pedido4).build();
            DetallePedido detallePedido8 = DetallePedido.builder().articulo(HamburguesaClasica).cantidad(1).subTotal(HamburguesaClasica.getPrecioVenta()).pedido(pedido4).build();
            pedido4.getDetallePedidos().add(detallePedido7);
            pedido4.getDetallePedidos().add(detallePedido8);
            pedido4.setTotal(pedido4.getDetallePedidos().stream().mapToDouble(detalle -> detalle.getSubTotal()).sum() * 0.9);

            Factura factura4 = Factura.builder().fechaFacturacion(LocalDate.of(2024, 2, 13)).formaPago(FormaPago.MercadoPago).mpMerchantOrderId(3).mpPaymentId(3).mpPaymentType("mercado pago").mpPreferenceId("0003").totalVenta(2500d).pedido(pedido4).build();
            pedido4.setFactura(factura4);
            pedidoRepository.save(pedido4);

            // Creación del pedido 5
            Pedido pedido5 = Pedido.builder()
                    .domicilio(domicilioCliente1)
                    .estado(Estado.PAGO_PENDIENTE)
                    .formaPago(FormaPago.MercadoPago)
                    .fechaPedido(new Date(124, 5, 1))
                    .horaEstimadaFinalizacion(LocalTime.of(1, 2, 3))
                    .sucursal(sucursalChacras)
                    .tipoEnvio(TipoEnvio.Delivery)
                    .total(0d)
                    .totalCosto(1466d)
                    .cliente(cliente5)
                    .empleado(empleadoCajero)
                    .build();
            DetallePedido detallePedido9 = DetallePedido.builder().articulo(HamburguesaPicante).cantidad(1).subTotal(HamburguesaPicante.getPrecioVenta()).pedido(pedido5).build();
            DetallePedido detallePedido10 = DetallePedido.builder().articulo(PizzaCarneMolidaPimientos).cantidad(1).subTotal(PizzaCarneMolidaPimientos.getPrecioVenta()).pedido(pedido5).build();
            pedido5.getDetallePedidos().add(detallePedido9);
            pedido5.getDetallePedidos().add(detallePedido10);
            pedido5.setTotal(pedido5.getDetallePedidos().stream().mapToDouble(detalle -> detalle.getSubTotal()).sum());

            Factura factura5 = Factura.builder().fechaFacturacion(LocalDate.of(2024, 2, 13)).formaPago(FormaPago.MercadoPago).mpMerchantOrderId(4).mpPaymentId(4).mpPaymentType("mercado pago").mpPreferenceId("0004").totalVenta(2500d).pedido(pedido5).build();
            pedido5.setFactura(factura5);
            pedidoRepository.save(pedido5);

            // Creación del pedido 6
            Pedido pedido6 = Pedido.builder()
                    .domicilio(domicilioCliente1)
                    .estado(Estado.PAGO_RECHAZADO)
                    .formaPago(FormaPago.MercadoPago)
                    .fechaPedido(new Date(124, 5, 10))
                    .horaEstimadaFinalizacion(LocalTime.of(1, 2, 3))
                    .sucursal(sucursalChacras)
                    .tipoEnvio(TipoEnvio.Delivery)
                    .total(0d)
                    .totalCosto(1108d)
                    .cliente(cliente6)
                    .empleado(empleadoCajero)
                    .build();
            DetallePedido detallePedido11 = DetallePedido.builder().articulo(HamburguesaPicante).cantidad(1).subTotal(HamburguesaPicante.getPrecioVenta()).pedido(pedido6).build();
            DetallePedido detallePedido12 = DetallePedido.builder().articulo(HamburguesaChampinones).cantidad(1).subTotal(HamburguesaChampinones.getPrecioVenta()).pedido(pedido6).build();
            pedido6.getDetallePedidos().add(detallePedido11);
            pedido6.getDetallePedidos().add(detallePedido12);
            pedido6.setTotal(pedido6.getDetallePedidos().stream().mapToDouble(detalle -> detalle.getSubTotal()).sum());

            Factura factura6 = Factura.builder().fechaFacturacion(LocalDate.of(2024, 2, 13)).formaPago(FormaPago.MercadoPago).mpMerchantOrderId(5).mpPaymentId(5).mpPaymentType("mercado pago").mpPreferenceId("0005").totalVenta(2500d).pedido(pedido6).build();
            pedido6.setFactura(factura6);
            pedidoRepository.save(pedido6);
        };
    }
}