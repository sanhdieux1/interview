package filter;

import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;

public class CrossOriginAccessControlFilter implements Filter {

    @Override
    public Result filter(FilterChain filterChain, Context context) {
        Result result = filterChain.next(context);
        result.addHeader("Access-Control-Allow-Origin", "*");
	result.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
	result.addHeader("Access-Control-Max-Age", "3600");
	result.addHeader("Access-Control-Allow-Headers", "Content-type, X-Foo-for-demo-only");
        return result;
    }
    
}