package io.github.newhoo.restkit.common;

import com.intellij.openapi.application.ReadAction;
import com.intellij.psi.PsiElement;
import io.github.newhoo.restkit.restful.ParamResolver;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Restful Api Item from PsiElement which is Navigable
 * <p>
 * PSI access must run inside a read-action (EDT alone is not enough on recent IntelliJ platforms).
 *
 * @author newhoo
 */
@NotProguard
@Getter
public class PsiRestItem extends RestItem {

    /**
     * Java: PsiMethod
     * Kotlin: KtNamedFunction
     */
    @NotNull
    private final PsiElement psiElement;
    @NotNull
    private final ParamResolver resolver;

    public PsiRestItem(@NotNull String url, String requestMethod, @NotNull String moduleName, @NotNull String framework, @NotNull PsiElement psiElement, @NotNull ParamResolver resolver) {
        super(url, requestMethod, ReadAction.compute(() -> resolver.buildDescription(psiElement)), moduleName, framework);
        this.psiElement = psiElement;
        this.resolver = resolver;
    }

    @NotNull
    @Override
    public List<KV> getHeaders() {
        return ReadAction.compute(() -> resolver.buildHeaders(psiElement));
    }

    @NotNull
    @Override
    public List<KV> getParams() {
        return ReadAction.compute(() -> resolver.buildParams(psiElement));
    }

    @NotNull
    @Override
    public String getBodyJson() {
        return ReadAction.compute(() -> resolver.buildRequestBodyJson(psiElement));
    }

    @Override
    public boolean isValid() {
        return ReadAction.compute(() -> psiElement.isValid());
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public boolean canDelete() {
        return false;
    }
}
