import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { retry, catchError, shareReplay } from 'rxjs/operators';

import { ActionHandler } from '../models';
import { UrlProviderService } from '../services';

@Injectable({
  providedIn: 'root'
})
export class ActionHandlerService {
  constructor(private http: HttpClient,
              private urlProviderService: UrlProviderService) { }

  public getActionHandlers(): Observable<ActionHandler[]> {
    return this.http.post<ActionHandler[]>(this.urlProviderService.getActionHandlerSearchUrl(), {}).pipe(
      retry(1),
      catchError(this.handleError),
      shareReplay()
    );
  }

  private handleError(error: any): Observable<any> {
    return throwError(error);
  }
}
