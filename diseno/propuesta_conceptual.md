# Ale y Leo — Propuesta conceptual de mockups

Oct 22, 2026 · @Emanuel

## 1. Propósito y alcance

Este documento fija la dirección visual de los 6 mockups de alta fidelidad de la tarea DI-06. Una vez aprobado, las pantallas se construyen en HTML, y las fotos y los elementos artesanales se generan en Flow con prompts de KinoFrame.

**Qué son estos mockups:** páginas HTML a tamaño de teléfono, con las fuentes, los colores y los íconos reales, más las fotos y texturas generadas en Flow. Son a la vez los mockups de DI-06 y el prototipo HTML del plan, y sirven para alinear el diseño con Jim y para las diapositivas de la E2.

**Qué no son:** no son código Android. Jim los replica en Compose, y si algo del mockup no coincide con las tablas, mandan las tablas. Lo que Jim implementa en `Color.kt`, `Type.kt` y `Shape.kt` sale de las tablas de este documento (secciones 3 a 5), no de medir el mockup.

**Pantallas incluidas (las de DI-06):**

| # | Pantalla | Rol | Por qué es clave |
| --- | --- | --- | --- |
| P04 | Catálogo | Cliente | Primera impresión de la marca; define la tarjeta de artesanía |
| P05 | Detalle de artesanía | Cliente | Muestra la pieza y la decisión de compra |
| P07 | Carro | Cliente | Define cantidades, totales y el motivo gráfico |
| P08 | Checkout | Cliente | Formulario y resumen; patrón para los demás formularios |
| P15 | Admin: Pedidos | Admin | Define los chips de los 7 estados |
| P18 | Admin: Formulario de producto | Admin | Patrón de formulario de la administradora |

Las demás pantallas se derivan de estos patrones. La sección 7.2 propone sumar tres para mostrar los elementos artesanales.

### 1.1 Cobertura de los entregables de diseño de la arquitectura

Este trabajo cubre los 7 entregables de diseño de la E2 definidos en la sección 7.1 de la arquitectura (DI-01 a DI-07). DI-08 y DI-09 son de la entrega final y quedan fuera.

| ID | Entregable | Cómo se cubre | Formato |
| --- | --- | --- | --- |
| DI-01 | Moodboard y dirección visual | Concepto (sección 2) y dirección fotográfica (sección 6), más una lámina que reúne paleta, fotos, mesa, textura y sello | Página HTML |
| DI-02 | Logo e ícono de la app | `/KinoFrame logo`: logotipo, isotipo, ícono adaptativo de Android (capa frontal y fondo de 108 dp, con el isotipo dentro de la zona segura central de 66 dp) y versión para el Splash | SVG y PNG |
| DI-03 | Paleta y tipografía | Secciones 3 y 4, con una prueba tipográfica antes de cerrarlas | Este documento y una página HTML de prueba |
| DI-04 | Design system en Material 3 | Sección 5, en una página HTML con tokens y componentes, más los archivos `Color.kt`, `Type.kt` y `Shape.kt` listos para Jim | Página HTML y archivos Kotlin |
| DI-05 | Wireframes de las 19 pantallas | En HTML, en baja fidelidad (grises, sin fotos ni texturas) y con la navegación entre pantallas enlazada según el mapa de la sección 7.2 de la arquitectura | Prototipo HTML navegable |
| DI-06 | Mockups en alta fidelidad | Las 6 pantallas de DI-06 más las 3 de la sección 7.2 de este documento | Páginas HTML |
| DI-07 | Diapositivas de la E2 | Se arman con los wireframes, los mockups y el design system | Presentación |

**Orden:** DI-04 y DI-05 bloquean el trabajo de Jim (tareas AP-02 y AP-04 a AP-12), y sus fechas en el plan (20 y 21 de octubre) ya pasaron. Por eso van primero y en paralelo con Flow, porque no dependen de ninguna imagen generada. Los mockups (DI-06) se cierran cuando estén las fotos y los elementos artesanales.

## 2. Concepto rector: «La mesa del taller»

La app debe sentirse como la mesa de trabajo de un taller artesanal: una superficie clara y ordenada donde cada pieza se apoya sola, bajo buena luz. La interfaz es la mesa, con su fibra, su veta y sus marcas de uso, y la artesanía es lo único con color intenso.

De esta idea salen tres reglas que ordenan todas las decisiones siguientes:

1. **La pieza manda.** La fotografía de la artesanía es el elemento más grande y más rico de cada pantalla. La interfaz no compite con ella: las texturas artesanales viven en los bordes y en los momentos de pausa (cabeceras, estados vacíos, confirmaciones), nunca detrás de una foto ni con más contraste que el texto.
2. **Los colores vienen de los materiales.** Cada color de la paleta sale de una de las cuatro categorías del catálogo (cobre, lapislázuli, greda, lana). No hay colores ajenos al oficio.
3. **La mano se nota en un solo gesto.** La calidez artesanal no se reparte en adornos: vive en la tipografía de los títulos y en un sistema corto de elementos hechos a mano: la textura del fondo, la mesa del taller, la costura, las ilustraciones a tinta y el sello (sección 5).

**Tono:** cálido, sereno y directo. Ni rústico ni folclórico. Se descarta la estética de feria (banderines, guardas étnicas, texturas de saco o madera decorativa de catálogo), porque convierte la artesanía en souvenir y le quita valor a la pieza.

**Lo que se evita a propósito:** el aspecto de e-commerce genérico (azul corporativo, banners de oferta, etiquetas de descuento en rojo) y los clichés visuales chilenos (copihue, bandera, cordillera como decoración). El valor de Ale y Leo es el oficio, y eso se muestra con la pieza, no con símbolos.

## 3. Paleta

La paleta parte de la referencia de la E1 (cobre `#C86432`, lapislázuli `#205493`) pero oscurece el cobre a `#A84E24`. El cobre original tiene un contraste de 3,94:1 con texto blanco y no llega al mínimo WCAG AA de 4,5:1 para botones; el nuevo llega a 5,56:1.

### 3.1 Tokens de Material 3 (tema claro)

| Rol M3 | Nombre | Hex | Viene de | Uso | Contraste |
| --- | --- | --- | --- | --- | --- |
| primary | Cobre | `#A84E24` | Cobre | Botón principal, carro, precio destacado, pestaña activa | 5,56:1 con blanco |
| primaryContainer | Cobre claro | `#F6DCCB` | Cobre | Fondo del chip de categoría elegido, badge del carro | 11,68:1 con texto |
| secondary | Lapislázuli | `#1F4E8C` | Orfebrería | Enlaces, corazón de favorito activo, acciones secundarias | 8,31:1 con blanco |
| secondaryContainer | Lapislázuli claro | `#DCE6F3` | Orfebrería | Avisos informativos | 12,14:1 con texto |
| tertiary | Ocre de lana | `#7A5A1C` | Textil | Stock bajo («Quedan 2») | 6,35:1 con blanco |
| background / surface | Lino | `#F7F2EB` | Lana cruda | Fondo de todas las pantallas | — |
| surfaceVariant | Arcilla clara | `#EFE6DA` | Alfarería | Tarjetas, campos de texto, barra inferior | 12,39:1 con texto |
| onSurface | Greda negra | `#2A2420` | Greda de Quinchamalí | Texto principal e íconos | 13,75:1 con lino |
| onSurfaceVariant | Greda media | `#5E5249` | Alfarería | Texto secundario, etiquetas | 6,78:1 con lino |
| outline | Borde | `#8A7D71` | Alfarería | Bordes de campos, costura | 3,59:1 (no es texto) |
| error | Error | `#B3261E` | Material 3 | Errores de formulario | 6,54:1 con blanco |

No se usa negro puro (`#000000`) ni blanco puro como fondo. La greda negra y el lino mantienen la temperatura cálida en toda la app.

### 3.2 Chips de los 7 estados del pedido

Cada estado tiene su color, pero también su ícono y su texto, para que no dependa solo del color (accesibilidad para personas con daltonismo).

| Estado | Texto | Fondo | Ícono (Material Symbols) | Contraste |
| --- | --- | --- | --- | --- |
| GENERADA | `#5C544D` | `#ECE7E1` | `receipt_long` | 6,04:1 |
| PAGADA | `#1F4E8C` | `#DCE6F3` | `paid` | 6,59:1 |
| RECIBIDA | `#245E66` | `#D7EBEC` | `inventory_2` | 5,92:1 |
| EN PREPARACIÓN | `#7A5A1C` | `#F3E6C8` | `handyman` | 5,13:1 |
| FINALIZADA | `#2E6B3A` | `#DCEFDD` | `check_circle` | 5,31:1 |
| CANCELADA | `#6B6560` | `#E7E3DF` | `cancel` | 4,50:1 |
| RECHAZADA | `#9C2A20` | `#F7DCD8` | `block` | 5,85:1 |

Todos los contrastes cumplen WCAG AA (4,5:1) para texto de 14 sp. CANCELADA queda justo en el límite a propósito: es el estado menos importante y debe verse apagado.

**Tema oscuro:** queda fuera de los mockups. Si alcanza el tiempo, se genera con la herramienta Material Theme Builder a partir de estos mismos colores semilla.

## 4. Tipografía

Se usan dos familias de Google Fonts: **Fraunces** para títulos y **Work Sans** para todo lo demás. Ambas son gratuitas y se cargan en Compose con fuentes descargables o copiando los `.ttf` a `res/font`.

- **Fraunces (títulos):** es una serif suave, con remates redondeados y algo de irregularidad, como letra tallada. Es el gesto de la mano del concepto rector, y solo aparece en nombres de piezas, títulos de pantalla y precios grandes.
- **Work Sans (texto e interfaz):** es una sans-serif sobria y muy legible a tamaños chicos. Lleva botones, etiquetas, campos y texto corrido. No compite con Fraunces.

### 4.1 Escala tipográfica (roles de Material 3)

| Rol M3 | Familia | Peso | Tamaño / interlineado (sp) | Dónde |
| --- | --- | --- | --- | --- |
| headlineMedium | Fraunces | SemiBold 600 | 28 / 36 | Nombre de la pieza en el Detalle, total del carro |
| titleLarge | Fraunces | SemiBold 600 | 22 / 28 | Título de cada pantalla |
| titleMedium | Work Sans | Medium 500 | 16 / 24 | Nombre de la pieza en la tarjeta, títulos de sección |
| bodyLarge | Work Sans | Regular 400 | 16 / 24 | Descripción de la pieza |
| bodyMedium | Work Sans | Regular 400 | 14 / 20 | Texto secundario, datos del pedido |
| labelLarge | Work Sans | SemiBold 600 | 14 / 20 | Botones |
| labelMedium | Work Sans | Medium 500 | 12 / 16 | Chips, etiquetas de la barra inferior |

Los precios usan `titleMedium` en la tarjeta y `headlineMedium` en el Detalle, siempre en formato CLP con punto de miles (`$28.500`) y sin decimales.

### 4.2 Prueba tipográfica (sujeta a cambios)

La pareja Fraunces y Work Sans es la propuesta de partida, no una decisión cerrada. Antes de cerrar DI-03 armo una página HTML que muestra la misma tarjeta de artesanía y el mismo Detalle con cada combinación, sobre el lino y a tamaño real de teléfono.

| Opción | Títulos | Texto | Carácter |
| --- | --- | --- | --- |
| A (propuesta) | Fraunces | Work Sans | Serif suave, como letra tallada; texto sobrio |
| B | Young Serif | Work Sans | Serif más pesada y antigua, de imprenta de oficio |
| C | Literata | Figtree | Serif de libro, más sobria; texto más redondo y cercano |

Todas están en Google Fonts y se cargan igual en Compose, así que cambiar de opción después solo toca `Type.kt`.

## 5. Forma, espaciado, componentes y motivo gráfico

La app usa esquinas medianas, sin sombras marcadas y con mucho aire entre bloques. La profundidad se logra con cambios de tono (lino contra arcilla clara), no con elevación, para mantener la sensación de mesa plana.

### 5.1 Formas y espaciado

| Token | Valor | Dónde |
| --- | --- | --- |
| shape.small | 8 dp | Campos de texto, chips de estado |
| shape.medium | 12 dp | Tarjetas de artesanía, fotos, diálogos |
| shape.large | 20 dp | Hoja inferior del Detalle (bottom sheet) |
| shape.full | 50% | Botones principales, chips de categoría, botón flotante |
| Margen lateral de pantalla | 16 dp | Todas las pantallas |
| Separación entre tarjetas | 12 dp | Grilla del catálogo |
| Separación entre secciones | 24 dp | Detalle, Checkout, formularios |
| Elevación | 0 dp en tarjetas; 1 dp solo en la barra inferior | Sin sombras decorativas |

### 5.2 Componentes base

| Componente | Decisión |
| --- | --- |
| TarjetaArtesania | Foto 4:5 arriba con esquinas de 12 dp, corazón en la esquina superior derecha sobre un círculo de lino al 90%, nombre en `titleMedium` (máximo 2 líneas), precio en cobre. Si está agotada, la foto baja a 50% de saturación y aparece la etiqueta «Agotado» |
| Chip de categoría | Píldora con borde. Elegido: fondo cobre claro y texto greda negra. No elegido: borde `#8A7D71` y sin relleno |
| ChipEstado | Ícono más texto en mayúsculas pequeñas con los colores de la tabla 3.2 |
| Botón principal | Píldora cobre con texto blanco, alto 52 dp, ancho completo en el pie de pantalla |
| Botón secundario | Píldora con borde cobre y texto cobre |
| Campo de texto | Relleno arcilla clara, etiqueta flotante y borde inferior que se vuelve cobre al enfocar |
| Barra superior | Título en Fraunces a la izquierda, sin color de fondo (se funde con el lino) |
| Barra inferior | Fondo arcilla clara, 5 pestañas en el cliente y 4 en la admin, indicador activo en cobre claro |
| Selector de cantidad | Botones − y + circulares con borde y el número en el centro |

### 5.3 Motivo gráfico: la costura

El adorno que se dibuja en código es una línea punteada fina, como una puntada de hilo, en color `#8A7D71`, de 1 dp de grosor con trazos de 4 dp y espacios de 4 dp. Representa el trabajo hecho a mano.

Solo aparece en tres lugares, y nunca más:

1. Sobre el total del Carro, separando los ítems del resumen.
2. En el resumen del Checkout, como en una boleta.
3. En la pantalla de Confirmación, como borde del resumen bajo el sello (sección 5.5).

En Compose se dibuja con `drawLine` y `PathEffect.dashPathEffect`, unas 5 líneas de código, así que no agrega trabajo real a Jim.

### 5.4 Iconografía

Se usan los íconos **Material Symbols Rounded** en estilo contorno, grosor 400. Ya vienen con Compose y sus terminaciones redondeadas combinan con Fraunces. No se dibujan íconos propios.

### 5.5 Elementos artesanales (se generan en Flow)

Cuatro elementos llevan la mano del taller a la interfaz. Son justo los que el código no logra creíbles: fibras irregulares, veta de madera con desgaste, trazo de tinta y bordes de timbre. Todo lo demás se dibuja en HTML.

| Elemento | Qué es | Dónde aparece | Entrega |
| --- | --- | --- | --- |
| Textura de fondo | Papel de algodón hecho a mano con fibras de lino visibles, muy suave y repetible sin costuras | Fondo de todas las pantallas, sobre el color lino | WebP de 512 × 512 px que se repite en mosaico, máximo 150 KB |
| Mesa del taller | Cubierta de madera nativa gastada, con marcas de herramientas, una mancha de verdín de cobre, restos de greda seca y algunas virutas | Cabecera del Catálogo (banda de 140 dp con el wordmark), fondo de Splash y Login | WebP horizontal y vertical, máximo 300 KB cada uno |
| Ilustraciones a tinta | Dibujos de línea estilo grabado, en greda negra, de objetos del oficio: un telar con el hilo cortado, un canasto vacío, un carrete de lana, una lupa sobre una pieza de greda | Estados vacíos: Carro, Favoritos, Mis pedidos y búsqueda sin resultados | PNG con transparencia de 600 × 600 px |
| Sello del taller | Impresión de timbre de goma circular en tinta cobre, con bordes irregulares y carga de tinta desigual, sin texto | Pantalla de Confirmación; el código del pedido se escribe encima en HTML | PNG con transparencia de 800 × 800 px |

**Reglas para que no estorben:**

- **La textura de fondo es casi invisible.** Su variación de luminosidad no supera el 4% alrededor de `#F7F2EB`, para que los contrastes de la sección 3 se mantengan. Se nota al mirar de cerca, no a primera vista.
- **Nunca hay texto directo sobre la madera.** El wordmark de la cabecera va sobre una placa de lino o con una sombra suave. Cualquier otro texto va fuera de la banda.
- **Las ilustraciones y el sello no llevan texto.** Las palabras se componen en HTML, así no hay errores de ortografía y se pueden traducir o cambiar.
- **Un solo elemento artesanal fuerte por pantalla.** El Catálogo tiene la mesa; la Confirmación, el sello; los estados vacíos, su ilustración. Ninguna pantalla junta dos.

En Compose, la textura se repite con `ImageShader` y `TileMode.Repeated`, y el resto son imágenes normales en `res/drawable`. Para Jim no agrega lógica, solo recursos.

## 6. Dirección fotográfica de las artesanías

Todas las piezas se fotografían igual, como si fuera una sola sesión en el mismo taller: misma mesa, misma luz y mismo encuadre. Así la grilla del catálogo se lee ordenada aunque mezcle cobre, greda, plata y lana.

Esta dirección sirve para los mockups y también para las 20 imágenes de los datos sintéticos, que el plan ya definió generar con IA.

| Parámetro | Decisión | Por qué |
| --- | --- | --- |
| Superficie | Lino crudo sobre mesa de madera clara, apenas visible | Repite el color de fondo de la app (`#F7F2EB`); la foto se funde con la interfaz |
| Fondo | Pared lisa color arena, desenfocada | Sin objetos que distraigan |
| Luz | Ventana lateral desde la izquierda, suave, con rebote blanco a la derecha | Luz natural de taller; sombra corta y suave hacia la derecha |
| Ángulo | Tres cuartos picado, a unos 30° sobre la mesa | Muestra volumen y la cara superior de la pieza |
| Encuadre | Se genera en 3:4 vertical (Flow no ofrece 4:5) con la pieza centrada en el 55% del cuadro, y se recorta a 4:5 en código | Coincide con la tarjeta y deja aire para el corazón |
| Óptica | 100 mm macro, f/5.6 | La pieza entera nítida y el fondo suave |
| Color | Blancos cálidos, sin filtros ni viñeta | La pieza conserva su color real |
| Prohibido | Manos, personas, flores decorativas, props, texto, marcas de agua | La pieza manda (sección 2) |

### 6.1 Notas por categoría

| Categoría | Material dominante | Cuidado específico |
| --- | --- | --- |
| Orfebrería | Plata 950 y lapislázuli | Controlar los reflejos de la plata con el rebote; la piedra debe verse azul profundo, no turquesa |
| Cobre | Cobre martillado | Mostrar la textura del martillado con la luz rasante; evitar que se vea anaranjado plástico |
| Alfarería | Greda negra pulida y greda roja | En la greda negra, el brillo del pulido define la forma; sin él se ve como una mancha |
| Textil | Lana teñida y tejida en telar | Plegado suave para mostrar la trama; colores naturales (crudo, café, granate, añil) |

## 7. Pantallas

Cada pantalla tiene un solo foco visual, señalado abajo. La estructura va de arriba hacia abajo y usa los datos de muestra de la sección 8, para que los 6 mockups cuenten la misma historia.

### P04 Catálogo

**Foco:** la grilla de piezas.

1. Cabecera de 140 dp con la mesa del taller de fondo: el wordmark «Ale y Leo» en Fraunces sobre una placa de lino a la izquierda y el ícono del carro con badge «2» a la derecha.
2. Buscador en píldora de arcilla clara con el texto de ayuda «Buscar artesanías».
3. Fila de chips: Todas (elegido), Orfebrería, Cobre, Alfarería, Textil (los últimos cortados en el borde, para indicar que hay scroll).
4. Grilla de 2 columnas con 4 tarjetas visibles. Una tarjeta tiene el corazón activo (lapislázuli) y otra aparece agotada.
5. Barra inferior del cliente con Catálogo activo.

### P05 Detalle de artesanía

**Foco:** la foto de la pieza.

1. Foto a sangre que ocupa el 55% superior, con los botones volver y corazón flotando sobre círculos de lino.
2. Hoja de 20 dp que se monta sobre la foto con: categoría en `labelMedium`, nombre en Fraunces y precio en cobre.
3. Fila de datos: materiales o técnica y stock («Quedan 2», en ocre).
4. Descripción de dos o tres líneas.
5. Pie fijo: selector de cantidad a la izquierda y el botón «Agregar al carro» a la derecha.

### P07 Carro

**Foco:** el total.

1. Título «Tu carro» y la cantidad de ítems.
2. Dos ítems, cada uno con miniatura cuadrada de 72 dp, nombre, precio unitario, selector de cantidad e ícono de eliminar.
3. Costura (sección 5.3).
4. Subtotal y total, el total en Fraunces grande.
5. Botón «Continuar» a ancho completo.

### P08 Checkout

**Foco:** el resumen tipo boleta.

1. Título «Confirmar pedido».
2. Tarjeta de resumen: líneas de producto, costura y total.
3. Sección «Despacho» con dirección y comuna precargadas.
4. Campo de notas con el texto de ejemplo «Envolver para regalo».
5. Botón «Generar pedido».

### P15 Admin: Pedidos

**Foco:** los contadores por estado.

1. Título «El taller hoy» en Fraunces y, debajo, la fecha y una línea de resumen («3 pedidos por preparar»). Reemplaza al saludo con nombre: habla del trabajo del día, como la pizarra de un taller.
2. Fila de 3 tarjetas contador: Pagadas 3, En preparación 2, Finalizadas 12.
3. Chips de filtro con scroll horizontal.
4. Lista de 4 pedidos, cada uno con código, cliente, fecha, total y su ChipEstado (con cuatro estados distintos visibles).
5. Barra inferior de la admin con Pedidos activo.

### P18 Admin: Formulario de producto

**Foco:** el bloque de la imagen.

1. Barra superior «Nueva artesanía» con un botón cerrar.
2. Zona de imagen 4:5 con la foto ya elegida y dos botones píldora encima: «Galería» y «Tomar foto».
3. Campos: nombre, categoría (desplegable), materiales o técnica, descripción (multilínea), y precio y stock en la misma fila.
4. Pie fijo con el botón «Guardar» y el enlace «Desactivar» en greda media.

### 7.1 Decisiones comunes a las 6 pantallas

- Formato vertical de teléfono Android, con la barra de estado (hora 10:24, batería, señal) y la barra de gestos abajo.
- Sin marco de dispositivo: la pantalla va plana, a 360 × 800 dp, para poder ponerla luego en un marco dentro de las diapositivas. La textura de fondo de la sección 5.5 está en todas.
- Todo el texto de la interfaz va en español de Chile, en frases cortas.

### 7.2 Pantallas adicionales propuestas

De las 6 pantallas de DI-06, solo el Catálogo muestra un elemento artesanal fuerte. Para que la presentación muestre el sistema completo, propongo sumar tres pantallas cortas, que en HTML toman poco tiempo porque reutilizan los mismos componentes.

| # | Pantalla | Elemento artesanal | Estructura |
| --- | --- | --- | --- |
| P02 | Login | Mesa del taller | La mesa ocupa el tercio superior a sangre, con el wordmark sobre placa de lino; abajo, sobre lino, los campos email y contraseña, el botón «Ingresar» y el enlace «Crear cuenta» |
| P10 | Confirmación | Sello del taller | Sello cobre centrado con el código «PED-2026-0018» escrito encima en HTML, el chip PAGADA, el resumen con la costura y los botones «Ver mis pedidos» y «Seguir comprando» |
| P06 | Favoritos (vacío) | Ilustración a tinta | Ilustración del canasto vacío centrada, el texto «Aún no guardas piezas» y el botón secundario «Explorar el catálogo» |

## 8. Datos de muestra

Los 6 mockups comparten las mismas 4 piezas, una por categoría, y los mismos números. Así el carro suma lo que dice el catálogo y el pedido del checkout es el mismo que ve la admin. Todos los nombres de personas son ficticios.

### 8.1 Piezas

| Pieza | Categoría | Materiales o técnica | Precio | Stock | Estado en los mockups |
| --- | --- | --- | --- | --- | --- |
| Colgante Lapislázuli Cordillera | Orfebrería | Plata 950 y lapislázuli | $28.500 | 2 | Favorito; pieza del Detalle |
| Paila de Cobre Martillada | Cobre | Cobre martillado a mano | $34.900 | 4 | Pieza del Formulario de producto |
| Chanchito de Greda Negra | Alfarería | Greda negra pulida | $12.000 | 0 | Agotado en el Catálogo |
| Camino de Mesa en Telar | Textil | Lana de oveja teñida, telar | $22.000 | 3 | En el carro |

### 8.2 Carro y checkout

- Colgante Lapislázuli Cordillera, 1 unidad: $28.500.
- Camino de Mesa en Telar, 1 unidad: $22.000.
- **Total: $50.500.** No hay costo de despacho en el modelo de datos, así que no se muestra.
- Despacho: Los Aromos 1450, depto. 32, Providencia.

### 8.3 Pedidos de la admin

| Código | Cliente | Fecha | Total | Estado |
| --- | --- | --- | --- | --- |
| PED-2026-0018 | Camila Rojas | 22 oct | $50.500 | PAGADA |
| PED-2026-0017 | Tomás Fuentes | 21 oct | $34.900 | EN PREPARACIÓN |
| PED-2026-0016 | Valentina Soto | 20 oct | $12.000 | FINALIZADA |
| PED-2026-0015 | Matías Herrera | 20 oct | $22.000 | GENERADA |

El pedido 0018 es el mismo que Camila genera en el checkout, así que el recorrido cliente → admin queda coherente en la presentación.

**Por qué se evitan ciertas palabras:** los textos de muestra eligen comunas y nombres sin «ñ» y con pocas tildes. Los motores de imagen suelen equivocarse con esos caracteres, y cada error obliga a retocar la imagen.

## 9. Estrategia de producción

Las pantallas se construyen en HTML, y Flow genera solo lo que el código no logra creíble. Son 8 prompts para los mockups (4 fotos de piezas y 4 elementos artesanales) y 3 ilustraciones más que la app necesita después para sus otros estados vacíos.

### 9.1 Qué se hace dónde

| Elemento | Dónde se hace | Por qué |
| --- | --- | --- |
| Interfaz, tipografía, íconos, chips, costura | HTML (yo) | Colores y fuentes exactos, texto sin errores, editable |
| Fotos de las 4 piezas | Flow | Fotografía de producto |
| Textura de fondo | Flow | Fibras irregulares; el ruido en código se ve digital |
| Mesa del taller | Flow | Veta, desgaste y marcas reales |
| Ilustraciones a tinta | Flow | Trazo de tinta irregular |
| Sello del taller | Flow | Bordes de timbre y carga de tinta desigual |
| Retoque técnico de lo generado | Código (yo) | Volver repetible la textura, separar el fondo de ilustraciones y sello, verificar contrastes |

### 9.2 Motor y formato en Flow

| Elemento | Proporción | Modo de KinoFrame | Nota |
| --- | --- | --- | --- |
| Fotos de piezas | 3:4, recortada a 4:5 | Foto (9 bloques, con óptica y luz) | Dirección de la sección 6 |
| Mesa del taller | 16:9 para la cabecera y 9:16 para Login | Foto, vista cenital | Mismas semilla y referencia en las dos, para que sea la misma mesa |
| Textura de fondo | 1:1 | Foto macro, luz plana | El motor no garantiza que se repita sin costura; la ajusto después en código |
| Ilustraciones a tinta | 1:1 | Kit (asset aislado) | Sobre blanco puro; el fondo se separa en código |
| Sello | 1:1 | Kit (asset aislado) | Sobre blanco puro y sin letras |

El motor es Nano Banana en Google Flow (el modelo disponible, por ejemplo Nano Banana 2.1) y los prompts van en prosa en inglés. Ninguna imagen generada lleva texto: todas las palabras de la app viven en HTML.

### 9.3 Orden de trabajo

1. **Fotos de piezas (4 prompts).** Primero el colgante, que es la más difícil por los reflejos de la plata, y después las otras 3 con el colgante como referencia de luz. Sirven también como imágenes de los datos sintéticos.
2. **Mesa del taller (1 prompt, 2 formatos).** Vista cenital de la cubierta de madera.
3. **Textura de fondo (1 prompt).** Se prueba sobre el color lino antes de aprobarla.
4. **Ilustraciones (4 prompts).** Primero el canasto vacío, que aparece en el mockup de Favoritos; las otras 3 lo usan como referencia de estilo.
5. **Sello (1 prompt).**
6. **Pantallas HTML.** Cuando me subas las imágenes aprobadas, las proceso y armo las 9 pantallas (las 6 de DI-06 más las 3 de la sección 7.2).

**Logo (en paralelo a los pasos 1 a 5).** Se crea con `/KinoFrame logo`. En Flow solo se explora el símbolo, y el que se apruebe se pasa a vector. El nombre «Ale y Leo» no se genera: lo compongo en SVG con Fraunces, para que la ortografía y el espaciado sean exactos. El logo tiene que estar listo antes del paso 6, porque aparece en el Catálogo, el Login y el ícono de la app.

### 9.4 Cómo se mantiene la coherencia

- **Bloques fijos por familia:** un bloque fotográfico para fotos, mesa y textura (luz cálida de ventana, paleta de materiales con hex) y un bloque de tinta para ilustraciones y sello (trazo, grosor, color). Cada bloque se copia sin cambios en todos los prompts de su familia.
- **Primera pieza aprobada como referencia:** el colgante para las fotos y el canasto para las ilustraciones, con su rol declarado en el prompt.
- **Una variable por iteración:** si algo sale mal, se corrige un solo aspecto por vuelta.

## 10. Riesgos y límites

Al pasar las pantallas a HTML desaparece el riesgo mayor del plan anterior: el texto mal escrito y la deriva de color. Lo que queda son riesgos de los elementos generados, y casi todos se corrigen procesando la imagen en código.

| Riesgo | Dónde aparece | Mitigación |
| --- | --- | --- |
| La textura muestra costuras al repetirse | Fondo de todas las pantallas | La vuelvo repetible en código (desplazamiento y fundido de bordes) y la pruebo en mosaico antes de usarla |
| La textura queda muy marcada y baja el contraste del texto | Fondo | Ajusto su intensidad hasta el límite del 4% y vuelvo a medir los contrastes de la sección 3 |
| Las ilustraciones o el sello no traen transparencia | Estados vacíos, Confirmación | Se generan sobre blanco puro. En HTML se integran con modo de fusión multiplicar, y para Android extraigo la transparencia desde la luminosidad (funciona porque son de un solo color) |
| El sello aparece con letras inventadas | Confirmación | El prompt pide «sin letras ni números»; si aparecen, se borran con edición de esa zona |
| Las ilustraciones no parecen del mismo dibujante | Estados vacíos | El canasto aprobado es referencia de estilo de las otras 3 |
| La mesa se ve rústica o de feria | Cabecera y Login | Desgaste moderado y marcas de oficio concretas, sin props; solo en esos dos lugares |
| Las imágenes pesan demasiado para la app | Recursos de Android | Exportación en WebP con los límites de la tabla 5.5 |

**Límite del método:** las imágenes de Flow no se editan por capas. Si la mesa o el sello necesitan un cambio grande, se regeneran, pero el resto de la pantalla no se toca porque vive en HTML.

## 11. Decisiones a aprobar

Con estas respuestas escribo los prompts de Flow. Las que no marques se toman tal como están en el documento.

- [x] **Concepto:** «La mesa del taller», con sus tres reglas (sección 2).
- [x] **Paleta:** cobre oscurecido a `#A84E24` en vez del `#C86432` de la E1 (sección 3).
- [x] **Tipografía:** Fraunces para títulos y Work Sans para el resto, a confirmar con la prueba de la sección 4.2.
- [x] **Costura y artesanía:** la costura solo en Carro, Checkout y Confirmación (sección 5.3), más los elementos artesanales (textura de fondo, mesa del taller, ilustraciones a tinta y sello), con uno solo fuerte por pantalla (sección 5.5).
- [x] **Pantallas:** las 6 de DI-06 más Login, Confirmación y Favoritos vacío (secciones 1 y 7.2).
- [x] **Producción:** pantallas en HTML; en Flow solo las fotos y los elementos artesanales, empezando por las 4 fotos (sección 9). Cubre DI-01 a DI-07 de la arquitectura (sección 1.1).
- [x] **Motor:** Nano Banana Pro en Google Flow (sección 9.1).
- [x] **Logo:** todavía no existe, así que se crea dentro de este trabajo, antes de armar las pantallas (sección 9.3), con `/KinoFrame logo`.
- [x] **Título de la admin:** la pantalla de la admin no saluda por nombre; se titula «El taller hoy» (sección 7, P15).
- [x] **Piezas de muestra:** se inventan; son las 4 de la tabla 8.1 y se cambian cuando haya piezas reales.
