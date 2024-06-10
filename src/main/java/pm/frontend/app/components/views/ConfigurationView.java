package pm.frontend.app.components.views;

import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import pm.frontend.app.components.customization.ThemeConfig;
import pm.frontend.app.components.standard.buttons.IToogleDarkObserver;
import pm.frontend.app.components.standard.components.ComboBoxWithText;

@PageTitle("Configuración")
@Route(value = "/configuration", layout = AppView.class)
public class ConfigurationView extends VerticalLayout {

	private static final long serialVersionUID = 736252245508591537L;

	public ConfigurationView() {
		
		setSizeFull();
		
		TabSheet tabSheet = new TabSheet();
		
		tabSheet.setMaxWidth("100%");
		tabSheet.setWidth("100%");
		tabSheet.setMaxHeight("100%");
	        
		tabSheet.add("Personalización", new LazyComponent(
                () -> new Customization()));
		tabSheet.add("Ayuda", new LazyComponent(
                () -> new Help()));
		tabSheet.add("FAQ", new LazyComponent(
                () -> new FAQ()));
		tabSheet.add("Acerda de", new LazyComponent(
                () -> new About()));
 
        add(tabSheet);
	    
	}
	
	private class LazyComponent extends Div {
		
        private static final long serialVersionUID = 8854973147025065218L;

		public LazyComponent(
                SerializableSupplier<? extends Component> supplier) {
            addAttachListener(e -> {
                if (getElement().getChildCount() == 0) {
                    add(supplier.get());
                }
            });
        }
    }
	
	private class Customization extends VerticalLayout  implements IToogleDarkObserver {
		
		private static final long serialVersionUID = -1517414608442467863L;

		private ThemeConfig themeConfig;
		
		private Checkbox darkMode;
		private ComboBoxWithText<String> lenguage;
		
		public Customization() {
			
			setSizeFull();
			getStyle().setPadding("var(--lumo-space-m) 0");
			
			themeConfig = ThemeConfig.getCurrent();
			themeConfig.addObserver(this);
			
			darkMode = new Checkbox(themeConfig.isDarkMode());
			darkMode.setLabel("Aplicación en modo oscuro");
			
			darkMode.addClickListener(vc -> {
				if (themeConfig.isDarkMode() != darkMode.getValue().booleanValue()) {
					themeConfig.toggleToDarkLightMode();
				}
			});
			
			Span lenguageText = new Span("Seleccione el idioma en el que se mostrará la aplicación:");
			List<String> items = List.of("Español (ES)");
			ComboBox<String> lenguageSelector = new ComboBox<>();
			lenguageSelector.setItems(items);
			lenguageSelector.setValue(items.get(0));
			lenguage = new ComboBoxWithText<String>(lenguageText, lenguageSelector);			
			
			add(darkMode, lenguage);
			
		}

		@Override
		public void toogleDarkLightMode() {
			darkMode.setValue(themeConfig.isDarkMode());
		}
	}
	
	private class Help extends VerticalLayout {
		
		private static final long serialVersionUID = 3763805350391134807L;

		public Help() {
			
			setSizeFull();
			getStyle().setPadding("var(--lumo-space-m) 0");

			Div div = new Div();
			div.setSizeFull();
			
			Div header = new Div(
					new Div("En esta pestaña de la aplicación podrás encontrar una ayuda que te resolverá las dudas a cerca de las diferentes pantallas de la aplicación, su funcionamiento y uso esperado. La ayuda se divide en secciones, puedes pulsar sobre cualquiera de ellas para desplegar su contenido."),
					new Hr());
			header.getStyle().setMarginBottom("10px");
			
			Div body = new Div(new HelpBlock("Inicio de sesión", 
						new Div(
							new Paragraph("Para iniciar sesión es necesario rellenar el formulario con el nombre de usuario y la contraseña. En caso de que los datos no sean válidos, la aplicación mostrará un mensaje de error."),
							new Paragraph("Una vez introducidos correctamente, no sera necesario que vuelvas a iniciar sesión, la aplicacion recordara tu usuario salvo que cierres sesión de forma manual."),
							new Paragraph("En caso de olvidar el nombre de usuario o la contraseña, puedes comunicarlo a un superior para que te revise el nombre de usuario y/o resetee la contraseña.")
								)),
				new HelpBlock("Barra de navegación superior",
						new Div(
								new Paragraph("La barra de navegacion superior estará disponible todo el tiempo que tengas la sesión iniciada y te facilitará acceso a ciertas funciones de manera rápida."),
								new Paragraph("A la izquierda se muestra un botón para abrir o cerrar la barra de navegación lateral."),
								new Paragraph("A la derecha se muestra información de tu usuario:"),
								new UnorderedList(
										new ListItem("Si no estas usando un dispositivo móvil, se mostrarán unos iconos con acceso rápido, como el de cambiar entre modo claro/oscuro."),
										new ListItem("Dos iconos, el buzón y las notificaciones. Son un acceso directo al apartado de buzón y notificaciones respectivamente. A medida que tengas avisos, se indicará con un número de notificaciones pendientes de revisar."),
										new ListItem("Un icono de configuración, que es un acceso directo al apartado de configuración, donde personalizar la aplicación."),
										new ListItem("Información de tu usuario, tu nombre y tu icono. Si pulsas sobre esta información varas un listado con opciones a escoger. Entre visitar tu perfil, acceso rápido a configuración, y la posibilidad de cerrar la sesión de forma manual.")
										)
									)),
				new HelpBlock("Barra de navegación lateral",
						new Div(
								new Paragraph("La barra de navegación lateral te permitirá navegar entre los diferentes apartados de la aplicación. Se puede acceder a ella pulsando el icono de menú de la barra superior y consta de dos partes prinicipales."),
								new UnorderedList(
										new ListItem("Parte central: En ella verás todas las opciones de la aplicación, con iconos desplegables y scroll vertical en caso de que la información que veas no quepa en la pantalla."),
										new ListItem("Parte inferior: Estara fija y en ella tendrás otra forma de acceder a los accesos directos de tu usuario, igual que en la barra de navegacion superior. Las opciones disponibles seran visitar tu perfil, acceso rápido a configuración, y la posibilidad de cerrar la sesión de forma manual.")
										)
								)),
				new HelpBlock("Inicio",
						new Div(
								new Paragraph("En la pestaña de inicio podrás ver información relevante de forma rápida."),
								new Paragraph("En el primer recuadro podrás ver información para tu usuario, como alguna notificación o aviso importantes que te hayan llegado."),
								new Paragraph("Los demas recuadros se componen de gráficas que analizan los datos de la aplicación. Por ejemplo, podrás ver un historico con las horas fichadas los últimos días.")
								)),
				new HelpBlock("Fichar",
						new Div(
								new Paragraph("En esta pestaña es donde realizarás los fichajes de registro de jornada. Para ello deberás seguir el siguiente procedimiento: "),
								new OrderedList(
										new ListItem("Seleccionar el día sobre la que quieres imputar datos"),
										new ListItem("Una vez seleccionado el día, justo debajo podrás revisar el estado actual. Si aun no has fichado, estas en desanso, etc."),
										new ListItem("Si en ese día ya has fichado un finalizar jornada, no podrás realizar más registros en ese día, deberás seleccionar otro día. En caso de que hayas cometido algun error, tendrás que hablar con los responsables para que lo revisen."),
										new ListItem("Segun el estado en el que se encuentre tu jornada, se habilitaran los botónes que tengas opcion de pulsar. Por ejemplo, si no has comenzado la jornada aún, solamente podrás pulsar sobre el botón de empezar jornada."),
										new ListItem("Una vez hagas cualquier imputación, verás que la pantalla se recarga y te actualizará el estado teniendo en cuenta estos nuevos datos.")
										)
								)),
				new HelpBlock("Empleados",
						new Div(
						new Paragraph("La vista de empleados te mostrará en vista de tabla todos aquellos empleados que existan en el sistema."),
						new Paragraph("Esta vista tiene una serie de opciones en su parte superior que podrás utilizar para gestionar los datos: "),
						new UnorderedList(
								new ListItem("Si no usando un dispositivo móvil, tendrás un buscador simple que servirá para filtrar los empleados por nombre."),
								new ListItem("A continuaciónaparecerá un icono que te permitirá mostrar u ocultar columnas de la tabla para mostrar los datos a tu gusto."),
								new ListItem("Despues verás un icono de filtro avanzado, que te permitirá buscar datos por más campos a parte del nombre."),
								new ListItem("Por último, aparece un botón de '+', que te permitirá crear nuevos empleados.")
								),
						new Paragraph("Si seleccionas cualquier empleado se abrirá su vista de detalle. Aquí podrás modificar cualquier dato del empleado seleccionado y guardarlo utilizando el botón de guardar. En caso de que alguno de los datos introducidos no sea válido, por ejemplo, un DNI con formato inválido, la aplicación te mostrará una ventana de aviso hasta que revises los datos."),
						new Paragraph("Si solamente quieres revisar los datos del empleado, pero no modificar nada, puedes pulsar sobre el botón de cancelar, o usar la tecla 'esc', una vez revisados los datos que te interesen."),
						new Paragraph("Si quieres borrar el empleado seleccionado, podrás pulsar sobre el botón de 'borrar', ojo al usar esta opción, debes usarla solamente cuando sea estrictamente necesario, porque ese empleado puede tener registros de fichajes que quieras mantener. Si lo que quieres es simplemente que ese usuario ya no pueda acceder a la aplicación, revisa el apartado de SEGURIDAD > USUARIOS, donde se explica como realizar esta operación.")
						)),

				new HelpBlock("Horarios",
						new Div(
						new Paragraph("La sección de horarios consta de tres apartados, que podrás revisar individualmente."),
						new HelpBlock("Calendarios",
								new Div(
										new Paragraph("La vista de calendarios te mostrará en vista de tabla todos aquellos calendarios que existan en el sistema."),
										new Paragraph("Esta vista tiene una serie de opciones en su parte superior que podrás utilizar para gestionar los datos: "),
										new UnorderedList(
												new ListItem("Si no estas usando un dispositivo móvil, tendrás un buscador simple que servirá para filtrar los calendarios por nombre."),
												new ListItem("A continuación aparecerá un icono que te permitirá mostrar u ocultar columnas de la tabla para mostrar los datos a tu gusto."),
												new ListItem("Despues verás un icono de filtro avanzado, que te permitirá buscar datos por más campos a parte del nombre."),
												new ListItem("Por último, aparece un botón de '+', que te permitirá crear nuevos calendarios.")
												),
										new Paragraph("Si seleccionas cualquier calendario se abrirá su vista de detalle. Aquí podrás modificar cualquier dato del calendario seleccionado y guardarlo utilizando el botón de guardar. En caso de que alguno de los datos introducidos no sea válido, por ejemplo, un nombre que ya existe, la aplicación te mostrará una ventana de aviso hasta que revises los datos."),
										new Paragraph("Si solamente quieres revisar los datos del calendario, pero no modificar nada, puedes pulsar sobre el botón de cancelar, o usar la tecla 'esc', una vez revisados los datos que te interesen."),
										new Paragraph("Si quieres borrar el calendario seleccionado, podrás pulsar sobre el botón de 'borrar', ojo al usar esta opción, debes usarla solamente cuando sea estrictamente necesario, porque ese calendario puede tener registros asociados que quieras mantener.")
										)),
						new HelpBlock("Jornadas",
								new Div(
										new Paragraph("La vista de jornadas te mostrará en vista de tabla todos aquellas jornadas que existan en el sistema."),
										new Paragraph("Esta vista tiene una serie de opciones en su parte superior que podrás utilizar para gestionar los datos: "),
										new UnorderedList(
												new ListItem("Si no estas usando un dispositivo móvil, tendrás un buscador simple que servirá para filtrar las jornadas por nombre."),
												new ListItem("A continuación aparecerá un icono que te permitirá mostrar u ocultar columnas de la tabla para mostrar los datos a tu gusto."),
												new ListItem("Despues verás un icono de filtro avanzado, que te permitirá buscar datos por más campos a parte del nombre."),
												new ListItem("Por último, aparece un botón de '+', que te permitirá crear nuevas jornadas.")
												),
										new Paragraph("Si seleccionas cualquier jornada se abrirá su vista de detalle. Aquí podrás modificar cualquier dato de la jornada seleccionado y guardarlo utilizando el botón de guardar. En caso de que alguno de los datos introducidos no sea válido, por ejemplo, un nombre que ya existe, la aplicación te mostrará una ventana de aviso hasta que revises los datos. También podrás ver los turnos asociados a esa jornada, asi como añadir turnos nuevos."),
										new Paragraph("Si solamente quieres revisar los datos de la jornada, pero no modificar nada, puedes pulsar sobre el botón de cancelar, o usar la tecla 'esc', una vez revisados los datos que te interesen."),
										new Paragraph("Si quieres borrar la jornada seleccionado, podrás pulsar sobre el botón de 'borrar', ojo al usar esta opción, debes usarla solamente cuando sea estrictamente necesario, porque esa jornada puede tener registros asociados que quieras mantener.")
										)),
						new HelpBlock("Turnos",
								new Div(
										new Paragraph("La vista de turnos te mostrará en vista de tabla todos aquellos turnos que existan en el sistema."),
										new Paragraph("Esta vista tiene una serie de opciones en su parte superior que podrás utilizar para gestionar los datos: "),
										new UnorderedList(
												new ListItem("Si no estas usando un dispositivo móvil, tendrás un buscador simple que servirá para filtrar los turnos por nombre."),
												new ListItem("A continuación aparecerá un icono que te permitirá mostrar u ocultar columnas de la tabla para mostrar los datos a tu gusto."),
												new ListItem("Despues verás un icono de filtro avanzado, que te permitirá buscar datos por más campos a parte del nombre."),
												new ListItem("Por último, aparece un botón de '+', que te permitirá crear nuevos turnos.")
												),
										new Paragraph("Si seleccionas cualquier turno se abrirá su vista de detalle. Aquí podrás modificar cualquier dato del turno seleccionado y guardarlo utilizando el botón de guardar. En caso de que alguno de los datos introducidos no sea válido, por ejemplo, un nombre que ya existe, la aplicación te mostrará una ventana de aviso hasta que revises los datos. También podrás ver los intervalos de tiemp asociados al turno, asi como añadir nuevos intervalos."),
										new Paragraph("Si solamente quieres revisar los datos del turno, pero no modificar nada, puedes pulsar sobre el botón de cancelar, o usar la tecla 'esc', una vez revisados los datos que te interesen."),
										new Paragraph("Si quieres borrar el turno seleccionado, podrás pulsar sobre el botón de 'borrar', ojo al usar esta opción, debes usarla solamente cuando sea estrictamente necesario, porque ese turno puede tener registros asociados que quieras mantener.")
										))
						)),
				new HelpBlock("Informes",
						new Div(
								new Paragraph("La sección de informes actualmente consta de un tipo de informe que se pueda generar."),
								new HelpBlock("Registro de fichajes",
										new Div(
												new Paragraph("La vista de registro te mostrará un formulario para introducir los datos sobre los que quieres generar el informe de asistencia."),
												new Paragraph("Para ello deberás seleccionar el empleado/empleados y el mes del que quieras obtener los informes. Posteriormente pulsar el botón de generar informe."),
												new Paragraph("En caso de que selecciones varios empleados, se generará un informe PDF de varias páginas, separadas para cada empleado.")
												))
								)),
				new HelpBlock("Seguridad",
						new Div(
						new Paragraph("La sección de seguridad consta de tres apartados, que podrás revisar individualmente."),
						new HelpBlock("Usuarios",
								new Div(
										new Paragraph("La vista de usuarios te mostrará en vista de tabla todos aquellos usuarios de seguridad que existan en el sistema."),
										new Paragraph("Esta vista tiene una serie de opciones en su parte superior que podrás utilizar para gestionar los datos: "),
										new UnorderedList(
												new ListItem("Si no estas usando un dispositivo móvil, tendrás un buscador simple que servirá para filtrar los usuarios por nombre."),
												new ListItem("A continuación aparecerá un icono que te permitirá mostrar u ocultar columnas de la tabla para mostrar los datos a tu gusto."),
												new ListItem("Despues verás un icono de filtro avanzado, que te permitirá buscar datos por más campos a parte del nombre."),
												new ListItem("Por último, aparece un botón de '+', que te permitirá crear nuevos usuarios.")
												),
										new Paragraph("Si seleccionas cualquier usuario se abrirá su vista de detalle. Aquí podrás modificar cualquier dato del usuario seleccionado y guardarlo utilizando el botón de guardar. En caso de que alguno de los datos introducidos no sea válido, por ejemplo, un nombre de usuario que ya existe, la aplicación te mostrará una ventana de aviso hasta que revises los datos."),
										new Paragraph("En este punto también vas a poder revisar los grupos a los que pertenece ese usuario, asi como añadir/quitarlo de cualquiera de ellos. Podrás hacer lo mismo con los roles que tenga dicho usuario."),
										new Paragraph("Si solamente quieres revisar los datos del usuario, pero no modificar nada, puedes pulsar sobre el botón de cancelar, o usar la tecla 'esc', una vez revisados los datos que te interesen."),
										new Paragraph("Si quieres borrar el usuario seleccionado, podrás pulsar sobre el botón de 'borrar', ojo al usar esta opción, debes usarla solamente cuando sea estrictamente necesario, porque ese usuario puede tener registros asociados que quieras mantener."),
										new Paragraph("OJO: Es muy probable que necesites desactivar algun usuario, pero como ya tiene registros de fichaje que no quieres borrar, no puedes borrarlo. Para eso, puedes usar la opcion de 'permitir inicio de sesion'. Si le quitas esta opcion al usuario, no podra iniciar en la aplicacion, pero los datos de fichajes se quedan almacenados por si fuera necesario revisarlos en el futuro.")

										)),
						new HelpBlock("Grupos",
								new Div(
										new Paragraph("La vista de grupos te mostrará en vista de tabla todos aquellos grupos de seguridad que existan en el sistema."),
										new Paragraph("Esta vista tiene una serie de opciones en su parte superior que podrás utilizar para gestionar los datos: "),
										new UnorderedList(
												new ListItem("Si no estas usando un dispositivo móvil, tendrás un buscador simple que servirá para filtrar los grupos por nombre."),
												new ListItem("A continuación aparecerá un icono que te permitirá mostrar u ocultar columnas de la tabla para mostrar los datos a tu gusto."),
												new ListItem("Despues verás un icono de filtro avanzado, que te permitirá buscar datos por más campos a parte del nombre."),
												new ListItem("Por último, aparece un botón de '+', que te permitirá crear nuevos grupos.")
												),
										new Paragraph("Si seleccionas cualquier grupo se abrirá su vista de detalle. Aquí podrás modificar cualquier dato del grupo seleccionado y guardarlo utilizando el botón de guardar. En caso de que alguno de los datos introducidos no sea válido, por ejemplo, un nombre que ya existe, la aplicación te mostrará una ventana de aviso hasta que revises los datos."),
										new Paragraph("En este punto también vas a poder revisar los usuarios que pertenecen al grupo y también añadir/quitar estos usuarios. Podrás hacer lo mismo con los roles que tenga dicho grupo."),
										new Paragraph("Si solamente quieres revisar los datos del grupo, pero no modificar nada, puedes pulsar sobre el botón de cancelar, o usar la tecla 'esc', una vez revisados los datos que te interesen."),
										new Paragraph("Si quieres borrar el grupo seleccionado, podrás pulsar sobre el botón de 'borrar', ojo al usar esta opción, debes usarla solamente cuando sea estrictamente necesario, porque ese grupo puede tener registros asociados que quieras mantener."),
										new Paragraph("OJO: Un usuario que pertenezca a un grupo, heredará todos los roles de ese grupo. Pero una vez que se elimine al usuario de dicho grupo, perderá estos roles salvo que se le hayan concedido en otro grupo, o a él individualmente.")
										)),
						new HelpBlock("Roles",
								new Div(
										new Paragraph("La vista de roles te mostrará en vista de tabla todos aquellos roles que existan en el sistema."),
										new Paragraph("Esta vista tiene una serie de opciones en su parte superior que podrás utilizar para gestionar los datos: "),
										new UnorderedList(
												new ListItem("Si no estas usando un dispositivo móvil, tendrás un buscador simple que servirá para filtrar los roles por nombre."),
												new ListItem("A continuación aparecerá un icono que te permitirá mostrar u ocultar columnas de la tabla para mostrar los datos a tu gusto."),
												new ListItem("Despues verás un icono de filtro avanzado, que te permitirá buscar datos por más campos a parte del nombre.")
												),
										new Paragraph("Si seleccionas cualquier rol se abrirá su vista de detalle. Aqui puedes revisar los usuarios que tienen concedido dicho rol, asi como los grupos que lo tienen."),
										new Paragraph("OJO: Los roles viene predefinidos por el sistema, se puede configurar los grupos y los usuarios que los tienen, pero no se pueden crear/editar/borrar roles.")
										))
						)));			
			
			div.add(
				header,
				body
				
			);
			
			Scroller scroller = new Scroller(div);
			
			add(scroller);
			
		}
		
	}
	
	private class FAQ extends VerticalLayout {
		
		private static final long serialVersionUID = 3763805350391134807L;

		public FAQ() {
			
			setSizeFull();
			getStyle().setPadding("var(--lumo-space-m) 0");

			Div div = new Div();
			div.setSizeFull();
			
			div.add(
				new HelpBlock("¿Pueden un usuario crearse una cuenta?", 
						"No, un usuario no puede crear ni dar de baja su propia cuenta."
						+ " Los administradores de la empresa seran los encargados de "
						+ "realizar estas operaciones desde el apartado de Seguridad > Usuarios."),
				new HelpBlock("¿Puede un usuario cambiar su contraseña?",
						"Si, desde el apartado de su perfil."),
				new HelpBlock("¿Que pasa si olvido mi contraseña?",
						"Los administradores pueden modificar las contraseñas de los usuarios."),
				new HelpBlock("¿Puedo configurar los permisos de los usuarios?",
						"Los administradores pueden modificar estos valores asignando grupos o "
						+ "roles desde el apartado de Seguridad. Los roles otorgan permisos para"
						+ " realizar ciertas acciones, y se pueden asignar directamente a los empleados,"
						+ " o a grupos de empleados. Si se da el último caso, los empleados por estar en ese"
						+ " grupo, automaticamente adquieren esos roles.")
			);
			
			Scroller scroller = new Scroller(div);
			
			add(scroller);
			
		}
		
	}
	
	private class HelpBlock extends VerticalLayout {
		
		private static final long serialVersionUID = 6500903169918153229L;
		
		private Details questionDetails;
		
		public HelpBlock(String question, String answer) {
			
			this(question, new Div(answer));
		}
		
		public HelpBlock(String question, Component answer) {
			
			this.setMargin(false);
			this.setPadding(false);
			
			Span summary = new Span(question);
	        summary.getStyle().set("white-space", "pre-wrap");
	        
	        Div body = new Div(answer);
	        body.getStyle().setPaddingLeft("20px");
	        
			questionDetails = new Details(summary, body);
			questionDetails.setOpened(false);
			
			add(questionDetails);
		}
	}
	
	private class About extends VerticalLayout {
		
		private static final long serialVersionUID = 2598252794649783711L;
		
		private H3 appName;
		private Span version;
		private Span date;
		private Span developer;
		private Span web;
		private Anchor webLink;
		private Span contact;
		private Anchor contactLink;
		
		public About() {
			
			setSizeFull();
			getStyle().setPadding("var(--lumo-space-m) 0");
			
			appName = new H3("Nombre: CESuite 4.0" );
			version = new Span("Versión: 1.0.0");
			date = new Span("Fecha: 01/06/2024");
			developer = new Span("Desarrollador: Diego Cortavitarte Zabala");
			web = new Span("Página web: ");
	        webLink = new Anchor("https://www.cesuite.es", "www.cesuite.es");
	        web.add(webLink);
			contact = new Span("Soporte: ");
			contactLink = new Anchor("mailto: support@cesuite.es", "support@cesuite.es");
			contact.add(contactLink);
			add(appName, version, date, developer, web, contact);
		}
		
	}
}
