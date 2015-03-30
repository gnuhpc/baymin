
package com.baymin.net.core;

import com.baymin.net.error.VolleyError;

public abstract class RequestCallback<ResponseType, ResultType> {

    public abstract ResultType doInBackground(ResponseType response);

    
    public abstract void onPostExecute(ResultType result);

    
    public void onError(VolleyError error) {
    }

}
