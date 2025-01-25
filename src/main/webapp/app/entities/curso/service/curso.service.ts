import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICurso, NewCurso } from '../curso.model';

export type PartialUpdateCurso = Partial<ICurso> & Pick<ICurso, 'id'>;

type RestOf<T extends ICurso | NewCurso> = Omit<T, 'dataCriacao' | 'dataInatividade'> & {
  dataCriacao?: string | null;
  dataInatividade?: string | null;
};

export type RestCurso = RestOf<ICurso>;

export type NewRestCurso = RestOf<NewCurso>;

export type PartialUpdateRestCurso = RestOf<PartialUpdateCurso>;

export type EntityResponseType = HttpResponse<ICurso>;
export type EntityArrayResponseType = HttpResponse<ICurso[]>;

@Injectable({ providedIn: 'root' })
export class CursoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/cursos');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(curso: NewCurso): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(curso);
    return this.http.post<RestCurso>(this.resourceUrl, copy, { observe: 'response' }).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(curso: ICurso): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(curso);
    return this.http
      .put<RestCurso>(`${this.resourceUrl}/${this.getCursoIdentifier(curso)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(curso: PartialUpdateCurso): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(curso);
    return this.http
      .patch<RestCurso>(`${this.resourceUrl}/${this.getCursoIdentifier(curso)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestCurso>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCurso[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCursoIdentifier(curso: Pick<ICurso, 'id'>): number {
    return curso.id;
  }

  compareCurso(o1: Pick<ICurso, 'id'> | null, o2: Pick<ICurso, 'id'> | null): boolean {
    return o1 && o2 ? this.getCursoIdentifier(o1) === this.getCursoIdentifier(o2) : o1 === o2;
  }

  addCursoToCollectionIfMissing<Type extends Pick<ICurso, 'id'>>(
    cursoCollection: Type[],
    ...cursosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const cursos: Type[] = cursosToCheck.filter(isPresent);
    if (cursos.length > 0) {
      const cursoCollectionIdentifiers = cursoCollection.map(cursoItem => this.getCursoIdentifier(cursoItem)!);
      const cursosToAdd = cursos.filter(cursoItem => {
        const cursoIdentifier = this.getCursoIdentifier(cursoItem);
        if (cursoCollectionIdentifiers.includes(cursoIdentifier)) {
          return false;
        }
        cursoCollectionIdentifiers.push(cursoIdentifier);
        return true;
      });
      return [...cursosToAdd, ...cursoCollection];
    }
    return cursoCollection;
  }

  protected convertDateFromClient<T extends ICurso | NewCurso | PartialUpdateCurso>(curso: T): RestOf<T> {
    return {
      ...curso,
      dataCriacao: curso.dataCriacao?.toJSON() ?? null,
      dataInatividade: curso.dataInatividade?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restCurso: RestCurso): ICurso {
    return {
      ...restCurso,
      dataCriacao: restCurso.dataCriacao ? dayjs(restCurso.dataCriacao) : undefined,
      dataInatividade: restCurso.dataInatividade ? dayjs(restCurso.dataInatividade) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestCurso>): HttpResponse<ICurso> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestCurso[]>): HttpResponse<ICurso[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
