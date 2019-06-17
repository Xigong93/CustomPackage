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
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import pokercc.android.custompakcage.CustomPackage;

/**
 * 自定义包名的注解处理器
 *
 * @author pokercc
 * 2019-6-17 21:23:43
 */
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
            Set<Modifier> modifiers = typeElement.getModifiers();
            // 进行检查
            // 不能被final 修饰
            if (modifiers.contains(Modifier.FINAL)) {
                throw new IllegalArgumentException(typeElement.getQualifiedName() + " must be not final class");
            }
            // 必须是public修饰 的
            if (!modifiers.contains(Modifier.PUBLIC)) {
                throw new IllegalArgumentException(typeElement.getQualifiedName() + " must be public class");
            }
            // 得有包名
            if (typeElement.getSimpleName().equals(typeElement.getQualifiedName())) {
                throw new IllegalArgumentException(typeElement.getQualifiedName() + " must be have package");

            }
            String fullName = packageName + "." + typeElement.getSimpleName();

            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "CustomPackage create class :" + fullName);

            String javaFileContent = "package " + packageName + " ;" +
                    "\n/* \n* create by " + CustomPackageProcessor.class.getSimpleName() + " don't modify!! \n*/\n" +
                    "public class " + typeElement.getSimpleName() + " extends " + typeElement.getQualifiedName() + " {}";


            Writer writer = null;
            try {
                writer = processingEnv
                        .getFiler()
                        .createSourceFile(fullName)
                        .openWriter()
                        .append(javaFileContent);
            } catch (IOException e) {
                throw new RuntimeException("unable to create source file " + fullName, e);
            } finally {
                try {
                    if (writer != null) writer.close();
                } catch (IOException e) {
                }
            }
        }
    };


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
