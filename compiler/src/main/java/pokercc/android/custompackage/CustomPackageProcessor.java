package pokercc.android.custompackage;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import pokercc.android.custompakcage.CustomPackage;

public class CustomPackageProcessor extends AbstractProcessor {
    Elements elementUtils;
    private ProcessingEnvironment processingEnv;
    private final Consumer<Element> mConsumer = new Consumer<Element>() {
        public void accept(Element element) {
            CustomPackage customPackage = element.getAnnotation(CustomPackage.class);
            String packageName = customPackage.value();
            if (packageName.isEmpty()) {
                packageName = customPackage.packageName();
            }
            if (packageName.isEmpty()) {
                throw new IllegalArgumentException("packageName can't be null or empty");
            }

            TypeElement typeElement;
            if (element instanceof TypeElement) {
                typeElement = (TypeElement) element;
            } else {
                typeElement = (TypeElement) element.getEnclosingElement();
            }
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                    "CustomPackage.packageName:" + packageName + ",subclassName:" + packageName);

            String javaFileContent = "package " + packageName + " ;" +
                    "\n/* \n* create by CustomPackageProcessor \n*/\n" +
                    "public class " + packageName + " extends " + typeElement.getQualifiedName() + " {}";


            Writer writer = null;
            try {
                writer = processingEnv
                        .getFiler()
                        .createSourceFile(packageName)
                        .openWriter()
                        .append(javaFileContent);
            } catch (IOException e) {
                e.printStackTrace();
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            } finally {
                try {
                    if (writer != null) writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public CustomPackageProcessor() {
    }

    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.elementUtils = processingEnv.getElementUtils();
        this.processingEnv = processingEnv;
    }

    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elememts = roundEnv.getElementsAnnotatedWith(CustomPackage.class);
        if (elememts != null) {
            for (Element elememt : elememts) {
                mConsumer.accept(elememt);

            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Set<String> getSupportedOptions() {
        return super.getSupportedOptions();
    }

    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotationTypes = new HashSet<String>();
        annotationTypes.add(CustomPackage.class.getCanonicalName());
        return annotationTypes;
    }

    interface Consumer<T> {
        void accept(T var1);

    }
}
