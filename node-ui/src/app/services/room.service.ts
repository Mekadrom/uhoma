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

  public getRooms(roomName: string | null): Observable<Room[]> {
    return this.http.get<Room[]>(this.getRoomSearchUrl(roomName))
    .pipe(
      retry(1),
      catchError(this.handleError),
      shareReplay()
    );
  }

  private getRoomSearchUrl(roomName: string | null): string {
    if (roomName == null) {
      return this.urlProvider.getRoomSearchUrl();
    } else {
      return this.urlProvider.getRoomSearchUrl() + '?name=' + roomName;
    }
  }

  private handleError(error: any): Observable<any> {
    return throwError(error);
  }
}
