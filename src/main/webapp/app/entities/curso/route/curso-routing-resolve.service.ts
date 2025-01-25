import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICurso } from '../curso.model';
import { CursoService } from '../service/curso.service';

@Injectable({ providedIn: 'root' })
export class CursoRoutingResolveService implements Resolve<ICurso | null> {
  constructor(protected service: CursoService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICurso | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((curso: HttpResponse<ICurso>) => {
          if (curso.body) {
            return of(curso.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
