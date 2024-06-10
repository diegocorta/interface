package pm.frontend.app.components.views;

import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.config.builder.ThemeBuilder;
import com.github.appreciated.apexcharts.config.theme.Mode;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.dom.Style.AlignItems;
import com.vaadin.flow.dom.Style.Display;
import com.vaadin.flow.dom.Style.JustifyContent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import pm.frontend.app.components.charts.AreaChartExample;
import pm.frontend.app.components.charts.BubbleChartExample;
import pm.frontend.app.components.charts.CandleStickChartExample;
import pm.frontend.app.components.charts.GradientRadialBarChartExample;
import pm.frontend.app.components.charts.HeatmapChartExample;
import pm.frontend.app.components.charts.HorizontalBarChartExample;
import pm.frontend.app.components.charts.LineMultiYAxesChartExample;
import pm.frontend.app.components.charts.MultiRadialBarChartExample;
import pm.frontend.app.components.charts.PieChartExample;
import pm.frontend.app.components.charts.ScatterChartExample;
import pm.frontend.app.components.charts.VerticalBarChartExample;
import pm.frontend.app.components.customization.ThemeConfig;
import pm.frontend.app.components.standard.buttons.IToogleDarkObserver;
import pm.frontend.app.components.standard.layouts.UserMainLayout;
import pm.frontend.app.logic.security.AuthenticationService;

@PageTitle("Main")
@Route(value = "/initial", layout = AppView.class)
@CssImport("./styles/chart.css")
@CssImport("./styles/grid.css")
public class MainView extends Div implements IToogleDarkObserver {

	private static final long serialVersionUID = -8025689310182504842L;

	private ThemeConfig themeConfig = ThemeConfig.getCurrent();
	
	private AuthenticationService authenticationService;
	
	private Div main;
	
	private UserMainLayout userMainLayout;
	
	private ApexCharts chart1;
	private ApexCharts chart2;
	private ApexCharts chart3;
	private ApexCharts chart4;
	private ApexCharts chart5;
	private ApexCharts chart6;
	private ApexCharts chart7;
	private ApexCharts chart8;
	private ApexCharts chart9;
	private ApexCharts chart10;
	private ApexCharts chart11;
	
	private Div div1;
	private Div div2;
	private Div div3;
	private Div div4;
	private Div div5;
	private Div div6;
	private Div div7;
	private Div div8;
	private Div div9;
	private Div div10;
	private Div div11;
	private Div div12;
		
	public MainView(AuthenticationService authenticationService) {

		themeConfig.addObserver(this);
			
		this.authenticationService = authenticationService;
		
		setClassName("gridcontainer");

		extracted();

	}

	private void extracted() {
		
		initCharts();
		
		userMainLayout = new UserMainLayout(authenticationService);
		
		main = new Div();
		main.addClassName("container");

		div1 = new Div();
		divCss(div1, 1);
		div1.add(userMainLayout);
		main.add(div1);
		
		div2 = new Div();
		divCss(div2, 2);
		div2.add(chart1);
		main.add(div2);
		
		div3 = new Div();
		divCss(div3, 3);
		div3.add(chart2);
		main.add(div3);
		
		div4 = new Div();
		divCss(div4, 4);
		div4.add(chart3);
		main.add(div4);
		
		div5 = new Div();
		divCss(div5, 5);
		div5.add(chart4);
		main.add(div5);
		
		div6 = new Div();
		divCss(div6, 6);
		div6.add(chart5);
		main.add(div6);
		
		div7 = new Div();
		divCss(div7, 7);
		div7.add(chart6);
		main.add(div7);
		
		div8 = new Div();
		divCss(div8, 8);
		div8.add(chart7);
		main.add(div8);
		
		div9 = new Div();
		divCss(div9, 9);
		div9.add(chart8);
		main.add(div9);
		
		div10 = new Div();
		divCss(div10, 10);
		div10.add(chart9);
		main.add(div10);
		
		div11 = new Div();
		divCss(div11, 11);
		div11.add(chart10);
		main.add(div11);
		
		div12 = new Div();
		divCss(div12, 12);
		div12.add(chart11);
		main.add(div12);

		add(main);
	}

	private void initCharts() {
		
		chart1 = new AreaChartExample().build();
		chartCss(chart1, 2);
		
		chart2 = new BubbleChartExample().build();
		chartCss(chart2, 3);

		chart3 = new CandleStickChartExample().build();
		chartCss(chart3, 4);
		
		chart4 = new GradientRadialBarChartExample().build();
		chartCss(chart4, 5);
		
		chart5 = new HeatmapChartExample().build();
		chartCss(chart5, 6);
		
		chart6 = new HorizontalBarChartExample().build();
		chartCss(chart6, 7);
		
		chart7 = new LineMultiYAxesChartExample().build();
		chartCss(chart7, 8);
		
		chart8 = new MultiRadialBarChartExample().build();
		chartCss(chart8, 9);
		
		chart9 = new PieChartExample().build();
		chartCss(chart9, 10);
		
		chart10 = new ScatterChartExample().build();
		chartCss(chart10, 11);
		
		chart11 = new VerticalBarChartExample().build();
		chartCss(chart11, 12);
		
	}
	
	private void chartCss(ApexCharts chart, int i) {
		
		chart.addClassName("chart"+i);
		chart.addClassName("chart");
		chart.setWidth("100%");
		chart.setHeight("100%");

		if (themeConfig.isDarkMode()) {
			chart.setTheme(ThemeBuilder.get().withMode(Mode.DARK).build());
		} else {
			chart.setTheme(ThemeBuilder.get().withMode(Mode.LIGHT).build());
		}
		
	}
	
	private void divCss(Div div, int i) {
		
		div.addClassName("item");
		div.addClassName("item" + i);

		div.getStyle().setDisplay(Display.FLEX)
			.setAlignItems(AlignItems.CENTER)
			.setJustifyContent(JustifyContent.CENTER);
	}

	@Override
	public void toogleDarkLightMode() {
		
		try {
			remove(main);
		} catch (Exception e) {
			
		}
		
		main = null;
		
		extracted();

	}
	
}

//chart.setSizeFull();
//chart.setTheme(ThemeBuilder.get().withMode(Mode.DARK).build());

//chart.addClassName("chart"+i);
//chart.addClassName("chart");
//chart.setWidth("100%");
//chart.setHeight("100%");
//chart3.getStyle().setBackgroundColor("red");
//chart3.getStyle().setJustifyContent(JustifyContent.CENTER);


//ApexCharts chart = new BubbleChartExample().build();
//chart.setTheme(ThemeBuilder.get().withMode(Mode.LIGHT).build());
////chart.getStyle().setBackgroundColor(null);
//chart.setWidth("600px");
//chart.setHeight("400px");
////chart.getStyle().setBorderRadius("30px");
////chart.getStyle().set( "border" , "6px dotted DarkOrange" ) ; 
//Button update = new Button("Update", buttonClickEvent -> {
//  // Actualiza la serie de datos con nuevos valores
//  Notification.show("The chart was updated!");
//});

//add(chart, update);
