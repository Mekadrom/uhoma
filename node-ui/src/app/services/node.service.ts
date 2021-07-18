import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';

import { Observable, throwError } from 'rxjs';
import { retry, catchError, shareReplay } from 'rxjs/operators';

import { Node } from '../models/node';
import { NodeAction } from '../models/node-action';
import { ActionParameterType } from '../models/action-parameter-type';

import { UrlProviderService } from './url-provider.service';

@Injectable({
  providedIn: 'root'
})
export class NodeService {
  constructor(private http: HttpClient, private urlProvider: UrlProviderService) { }

  public getNodes(): Observable<Node[]> {
    return this.http.post<Node[]>(this.urlProvider.getNodeSearchUrl(), {})
    .pipe(
      retry(1),
      catchError(this.handleError),
      shareReplay()
    );
  }

  public getActionParameterTypes(): Observable<ActionParameterType[]> {
    return this.http.post<ActionParameterType[]>(this.urlProvider.getNodeActionParameterTypeSearchUrl(), {})
    .pipe(
      retry(1),
      catchError(this.handleError),
      shareReplay()
    );
  }

  public saveNodeAction(nodeAction?: NodeAction): Observable<any> {
    console.log(JSON.stringify(nodeAction));
    return this.http.post<NodeAction>(this.urlProvider.getNodeActionSaveUrl(), nodeAction)
    .pipe(
      retry(0),
      catchError(this.handleError),
      shareReplay()
    );
  }

  private handleError(error: any): Observable<any> {
    return throwError(error);
  }
}
