import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { retry, catchError, shareReplay } from 'rxjs/operators';

import { Home } from '../models';
import { UrlProviderService } from '../services';

@Injectable({
  providedIn: 'root'
})
export class HomeService {
  constructor(private http: HttpClient,
              private urlProviderService: UrlProviderService) { }

  public getHomes(): Observable<Home[]> {
    return this.http.post<Home[]>(this.urlProviderService.getHomeSearchUrl(), {}).pipe(
      retry(1),
      catchError(this.handleError),
      shareReplay()
    );
  }

  public createNewHome(name: string): Observable<any> {
    return this.http.post<any>(this.urlProviderService.getHomeCreateUrl(), {name: name}).pipe(
      retry(1),
      catchError(this.handleError),
      shareReplay()
    );
  }

  private handleError(error: any): Observable<any> {
    return throwError(error);
  }
}
