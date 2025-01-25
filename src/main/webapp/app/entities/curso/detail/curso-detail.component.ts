import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICurso } from '../curso.model';

@Component({
  selector: 'jhi-curso-detail',
  templateUrl: './curso-detail.component.html',
})
export class CursoDetailComponent implements OnInit {
  curso: ICurso | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ curso }) => {
      this.curso = curso;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
