package test;

import java.io.IOException;
import java.util.Arrays;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.Histogram;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.Styler.LegendPosition;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws IOException {
        double[] xData = new double[] { 0.0, 1.0, 2.0 };
        double[] yData = new double[] { 2.0, 1.0, 0.0 };
        
        CategoryChart chart = new CategoryChartBuilder().width(800).height(600).title("Score Histogram").xAxisTitle("Score").yAxisTitle("Number").theme(ChartTheme.GGPlot2).build();

        // Customize Chart
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setHasAnnotations(true);
        // Series
        chart.addSeries("test 1", Arrays.asList(new String[] { "월", "화", "수", "목", "금" }), Arrays.asList(new Integer[] { 4, 5, 9, 6, 5 }));

        // Show it
        new SwingWrapper<CategoryChart>(chart).displayChart();

        // Save it
        BitmapEncoder.saveBitmap(chart, "./d/ds", BitmapFormat.PNG);

        // or save it in high-res
        BitmapEncoder.saveBitmapWithDPI(chart, "./Sample_Chart_300_DPI", BitmapFormat.PNG, 300);
    }
}
