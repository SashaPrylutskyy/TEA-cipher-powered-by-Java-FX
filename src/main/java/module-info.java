module com.op.teacipherfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires org.slf4j;

    opens com.op.teacipherfx to javafx.fxml;
    opens com.op.teacipherfx.service to org.junit.platform.commons;

    exports com.op.teacipherfx;
    exports com.op.teacipherfx.service;
}