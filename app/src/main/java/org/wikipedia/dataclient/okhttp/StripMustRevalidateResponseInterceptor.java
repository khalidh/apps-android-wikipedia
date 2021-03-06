package org.wikipedia.dataclient.okhttp;

import android.support.annotation.NonNull;

import org.wikipedia.dataclient.okhttp.util.HttpUrlUtil;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * This interceptor strips away the `must-revalidate` directive from the Cache-Control header,
 * since this directive prevents OkHttp from returning cached responses.  This directive makes
 * sense for a web browser, which unconditionally wants the freshest content from the network,
 * but is not necessary for our app, which needs to be more permissive with allowing cached content.
 */
class StripMustRevalidateResponseInterceptor implements Interceptor {
    @Override public Response intercept(@NonNull Interceptor.Chain chain) throws IOException {
        Response rsp = chain.proceed(chain.request());
        HttpUrl url = rsp.request().url();

        if (HttpUrlUtil.isRestBase(url) || HttpUrlUtil.isMobileView(url)) {
            String cacheControl = removeDirective(rsp.cacheControl().toString(), "must-revalidate");
            rsp = rsp.newBuilder().header("Cache-Control", cacheControl).build();
        }

        return rsp;
    }

    @NonNull private static String removeDirective(@NonNull String cacheControl, @NonNull String directive) {
        return cacheControl.replaceAll(directive + ", |,? ?" + directive, "");
    }
}
