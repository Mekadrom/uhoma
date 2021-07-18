import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';

import { Observable, throwError } from 'rxjs';
import { retry, catchError, shareReplay } from 'rxjs/operators';

import { Room } from '../models/room';
import { UrlProviderService } from './url-provider.service';

@Injectable({
  providedIn: 'root'
})
export class RoomService {
  constructor(private http: HttpClient, private urlProvider: UrlProviderService) { }

  public getRooms(searchCriteria: Room | null): Observable<Room[]> {
    return this.http.post<Room[]>(this.urlProvider.getRoomSearchUrl(), searchCriteria)
    .pipe(
      retry(1),
      catchError(this.handleError),
      shareReplay()
    );
  }

  private handleError(error: any): Observable<any> {
    return throwError(error);
  }
}
