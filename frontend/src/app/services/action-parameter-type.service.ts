import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { retry, catchError, shareReplay } from 'rxjs/operators';

import { ActionParameterType } from '../models';
import { UrlProviderService } from '../services';

@Injectable({
  providedIn: 'root'
})
export class ActionParameterTypeService {
  constructor(private http: HttpClient,
              private urlProviderService: UrlProviderService) { }

  public getActionParameterTypes(): Observable<ActionParameterType[]> {
    return this.http.post<ActionParameterType[]>(this.urlProviderService.getNodeActionParameterTypeSearchUrl(), {}).pipe(
      retry(1),
      catchError(this.handleError),
      shareReplay()
    );
  }

  private handleError(error: any): Observable<any> {
    return throwError(error);
  }
}
