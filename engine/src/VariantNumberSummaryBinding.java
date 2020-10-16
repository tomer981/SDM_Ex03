

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableNumberValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VariantNumberSummaryBinding extends DoubleBinding {

    private final DoubleProperty sumProperty;
    private final DoubleProperty avgProperty;
    private final List<ObservableNumberValue> numberDependencies;

    public VariantNumberSummaryBinding(ObservableNumberValue... observables) {
        bind(observables);
        sumProperty = new SimpleDoubleProperty();
        avgProperty = new SimpleDoubleProperty();

        numberDependencies = new ArrayList<>();
        numberDependencies.addAll(Arrays.asList(observables));
    }

    public void addNewDependency(ObservableNumberValue newDependency) {
        bind(newDependency);
        numberDependencies.add(newDependency);
    }

    public void removeDependency(ObservableNumberValue dependencyToRemove) {
        unbind(dependencyToRemove);
        numberDependencies.remove(dependencyToRemove);
    }

    @Override
    protected double computeValue() {

        sumProperty.set(
                numberDependencies
                        .stream()
                        .mapToDouble(obs -> obs.getValue().doubleValue())
                        .sum());

        numberDependencies
                .stream()
                .mapToDouble(obs -> obs.getValue().doubleValue())
                .average()
                .ifPresent(avgProperty::set);

        return sumProperty.get();
    }

    public DoubleProperty avgProperty() {
        return avgProperty;
    }

    public static void main(String[] args) throws IOException {
        DoubleProperty x = new SimpleDoubleProperty(10);
        DoubleProperty y = new SimpleDoubleProperty(20);
        DoubleProperty z = new SimpleDoubleProperty(30);

        VariantNumberSummaryBinding variantNumberSummaryBinding = new VariantNumberSummaryBinding(x, y);

        DoubleProperty sumProperty = new SimpleDoubleProperty();

        sumProperty.addListener( (obs, old, newVal) ->
                System.out.println("Current sum is " + newVal)
        );
        variantNumberSummaryBinding.avgProperty().addListener( (obs, old, newVal)  ->
                System.out.println("Current average is: " + newVal)
        );

        sumProperty.bind(variantNumberSummaryBinding);

        System.out.println("Adding Z dependency with value 30");
        variantNumberSummaryBinding.addNewDependency(z);
        variantNumberSummaryBinding.invalidate(); // trigger binding calculation as it is lazy evaluated by default

        System.out.println("changing Z dependency to value 60");
        z.set(60);
        System.out.println("Removing Y dependency (20)");
        variantNumberSummaryBinding.removeDependency(y);
        variantNumberSummaryBinding.invalidate();
    }
}
