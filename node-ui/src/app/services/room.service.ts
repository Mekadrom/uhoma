import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Room } from '../models/room';
import { Observable, throwError } from 'rxjs';
import { Injectable } from '@angular/core';
import { retry, catchError } from 'rxjs/operators';
import { HttpResponse } from "@angular/common/http";
import { ToastrService } from 'ngx-toastr';

@Injectable({
  providedIn: 'root'
})
export class RoomService {
  constructor(private http: HttpClient, private toastr: ToastrService) {}

    getRooms(roomName: string | null): Observable<Room[]> {
    return this.http.get<Room[]>(this.getRoomSearchUrl(roomName))
    .pipe(
      retry(1),
      catchError(this.handleError)
    );
  }

  getRoomSearchUrl(roomName: string | null): string {
    if (roomName == null) {
      return 'http://localhost:8080/room/search';
    } else {
      return 'http://localhost:8080/room/search?name=' + roomName;
    }
  }

  handleError(error: any): Observable<any> {
    return throwError(error);
  }
}
