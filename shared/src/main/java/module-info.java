module shared {
    requires transitive org.eclipse.yasson;
    requires transitive jakarta.json.bind;
    requires com.opencsv;
    exports at.nicoleperak.shared;
    opens at.nicoleperak.shared;
}