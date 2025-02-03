/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { BookRequest } from '../../models/book-request';

export interface SaveBook$Params {
  connectedUser: 'RSA' | 'DSS' | 'aNULL' | 'DH' | 'ECDH' | 'KRB5' | 'ECDSA' | 'PSK' | 'GOST94' | 'GOST01' | 'FZA' | 'SRP' | 'ANY';
      body: BookRequest
}

export function saveBook(http: HttpClient, rootUrl: string, params: SaveBook$Params, context?: HttpContext): Observable<StrictHttpResponse<number>> {
  const rb = new RequestBuilder(rootUrl, saveBook.PATH, 'post');
  if (params) {
    rb.query('connectedUser', params.connectedUser, {});
    rb.body(params.body, 'application/json');
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return (r as HttpResponse<any>).clone({ body: parseFloat(String((r as HttpResponse<any>).body)) }) as StrictHttpResponse<number>;
    })
  );
}

saveBook.PATH = '/books';
