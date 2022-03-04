import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { retry, catchError, shareReplay } from 'rxjs/operators';

import { Home, Node } from '../models';
import { UrlProviderService } from '../services';

@Injectable({
  providedIn: 'root'
})
export class NodeService {
  constructor(private http: HttpClient,
              private urlProviderService: UrlProviderService) { }

  public getNodes(homeSearchCriteria?: Home): Observable<Node[]> {
    return this.http.post<Node[]>(this.urlProviderService.getNodeSearchUrl(), {homeSeq: homeSearchCriteria?.homeSeq}).pipe(
      retry(1),
      catchError(this.handleError),
      shareReplay()
    );
  }

  public saveNodes(nodes?: Node[]): Observable<any> {
    return this.http.post<Node[]>(this.urlProviderService.getNodesSaveUrl(), nodes).pipe(
      retry(0),
      catchError(this.handleError),
      shareReplay()
    );
  }

  private handleError(error: any): Observable<any> {
    return throwError(error);
  }
}
