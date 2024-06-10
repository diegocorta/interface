package pm.frontend.app.components.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.Scroller.ScrollDirection;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoIcon;

import pm.frontend.app.components.customization.ThemeConfig;
import pm.frontend.app.components.standard.layouts.MainHeader;
import pm.frontend.app.logic.security.AuthenticationService;

@PageTitle("Pagina principal")
@Route("/main")
@CssImport("./styles/icon.css")
@CssImport("./styles/app-layout.css")
public class AppView extends AppLayout {

	private static final long serialVersionUID = -8025689310182504842L;

	private ThemeConfig themeConfig = ThemeConfig.getCurrent();
	private DrawerToggle toogle;
	private MainHeader header;
		
	public AppView(AuthenticationService authenticationService) {
		
		this.toogle = new DrawerToggle();
		Icon menuIcon = LumoIcon.MENU.create();
		menuIcon.setSize("var(--lumo-icon-size-l)");		
		
		toogle.setIcon(menuIcon);
		
		this.header = new MainHeader(toogle, authenticationService);
		
        themeConfig.initializeDarkMode();
        setPrimarySection(Section.NAVBAR);

        VerticalLayout nav = getSideNav();
        
        addToDrawer(nav);
        
        addToNavbar(false, header);
        
    }

    private VerticalLayout getSideNav() {
            
    	VerticalLayout vl = new VerticalLayout();
    	VerticalLayout vlScrollable = new VerticalLayout();
    	
		SideNav main = new SideNav();
		main.setSizeFull();
        main.addItem(new SideNavItem("Inicio", "/initial", VaadinIcon.HOME.create()));
        main.addItem(new SideNavItem("Fichar", "/journal", VaadinIcon.USER_CLOCK.create()));
		
        StreamResource iconResource = new StreamResource("inbox.svg",
			    () -> getClass().getResourceAsStream("/icons/inbox.svg"));
		
		SvgIcon inbox = new SvgIcon(iconResource);
		
        SideNav messages = new SideNav();
		messages.setLabel("Avisos");
		messages.setCollapsible(true);
		messages.addItem(new SideNavItem("Notificaciones", "/error", LumoIcon.BELL.create()));
		messages.addItem(new SideNavItem("Mensajes", "/error", inbox));
		messages.setSizeFull();
        
		SideNav administracion = new SideNav();
		administracion.setLabel("Administración");
		administracion.setSizeFull();
        
		SideNavItem calendarSection = new SideNavItem("Horarios");
			calendarSection.setPrefixComponent(LumoIcon.CALENDAR.create());
			calendarSection.addItem(new SideNavItem("Calendarios", "/calendars"));
			calendarSection.addItem(new SideNavItem("Jornadas", "/workdays"));
			calendarSection.addItem(new SideNavItem("Turnos", "/workshifts"));
		addScrollInViewOnCLick(calendarSection.getElement());
		
		SideNavItem documentSection = new SideNavItem("Informes");
	        documentSection.setPrefixComponent(VaadinIcon.FILE_O.create());
	        documentSection.addItem(new SideNavItem("Registro de fichajes", "/error"));
	    addScrollInViewOnCLick(documentSection.getElement());

	        
        SideNavItem adminSection = new SideNavItem("Seguridad");
        administracion.setCollapsible(true);
        addScrollInViewOnCLick(adminSection.getElement());
        
        adminSection.setPrefixComponent(VaadinIcon.KEY.create());
        adminSection.addItem(new SideNavItem("Usuarios", "/contacts"));
        adminSection.addItem(new SideNavItem("Grupos", "/groups"));
        adminSection.addItem(new SideNavItem("Roles", "/roles"));
        
        administracion.addItem(
        		new SideNavItem("Empleados", "/employees", VaadinIcon.USER.create()),
                calendarSection,
                documentSection,
                adminSection
                );
        
        StreamResource logout = new StreamResource("singout.svg",
			    () -> getClass().getResourceAsStream("/icons/singout.svg"));
		
		SvgIcon icon = new SvgIcon(logout);
        
        SideNav conf = new SideNav();
        conf.setWidthFull();
        conf.setLabel("Configuración");
        conf.addItem(new SideNavItem("Perfil", "/profile", LumoIcon.USER.create()));
        conf.addItem(new SideNavItem("Configuración", "/configuration", LumoIcon.COG.create()));
        conf.addItem(new SideNavItem("Cerrar sesión", "/logoutview", icon));
        
        vlScrollable.add(main, messages, administracion);
        vlScrollable.getStyle().setPadding("20px 0px");
        
        Scroller scroller = new Scroller(vlScrollable);
		scroller.setSizeFull();
		scroller.setScrollDirection(ScrollDirection.VERTICAL);
		scroller.getStyle()
			.set("border-bottom", "1px solid var(--lumo-contrast-20pct)")
			.set("border-top", "1px solid var(--lumo-contrast-20pct)");	
		
        vl.add(
        	new H2("CESuite 4.0"),
        	scroller,
        	conf
        	);
        
        vl.setJustifyContentMode(JustifyContentMode.BETWEEN);
		vl.setSizeFull();
        
        return vl;
    }
    
    private void addScrollInViewOnCLick(Element element) {
    	element.addPropertyChangeListener("expanded", e -> {
    		element.scrollIntoView();
    	});
    }
	
}
