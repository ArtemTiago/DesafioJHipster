import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { CursoFormService, CursoFormGroup } from './curso-form.service';
import { ICurso } from '../curso.model';
import { CursoService } from '../service/curso.service';
import { IArea } from 'app/entities/area/area.model';
import { AreaService } from 'app/entities/area/service/area.service';
import { StatusCurso } from 'app/entities/enumerations/status-curso.model';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ErrorModalComponent } from 'app/shared/modal/error-modal/error-modal.component';

@Component({
  selector: 'jhi-curso-update',
  templateUrl: './curso-update.component.html',
})
export class CursoUpdateComponent implements OnInit {
  isSaving = false;
  curso: ICurso | null = null;
  statusCursoValues = Object.keys(StatusCurso);

  areasSharedCollection: IArea[] = [];

  editForm: CursoFormGroup = this.cursoFormService.createCursoFormGroup();

  constructor(
    protected cursoService: CursoService,
    protected cursoFormService: CursoFormService,
    protected areaService: AreaService,
    protected activatedRoute: ActivatedRoute,
    private modalService: NgbModal
  ) {}

  compareArea = (o1: IArea | null, o2: IArea | null): boolean => this.areaService.compareArea(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ curso }) => {
      this.curso = curso;
      if (curso) {
        this.updateForm(curso);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const curso = this.cursoFormService.getCurso(this.editForm);
    if (curso.id !== null) {
      this.subscribeToSaveResponse(this.cursoService.update(curso));
    } else {
      this.subscribeToSaveResponse(this.cursoService.create(curso));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICurso>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: err => this.onSaveError(err),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(err: any): void {
    const modalRef = this.modalService.open(ErrorModalComponent, {
      centered: true,
      size: 'lg',
    });
    modalRef.componentInstance.message = err?.error?.message || 'Ocorreu um erro desconhecido. Tente novamente mais tarde.';
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(curso: ICurso): void {
    this.curso = curso;
    this.cursoFormService.resetForm(this.editForm, curso);

    this.areasSharedCollection = this.areaService.addAreaToCollectionIfMissing<IArea>(this.areasSharedCollection, curso.area);
  }

  protected loadRelationshipsOptions(): void {
    this.areaService
      .query()
      .pipe(map((res: HttpResponse<IArea[]>) => res.body ?? []))
      .pipe(map((areas: IArea[]) => this.areaService.addAreaToCollectionIfMissing<IArea>(areas, this.curso?.area)))
      .subscribe((areas: IArea[]) => (this.areasSharedCollection = areas));
  }
}
