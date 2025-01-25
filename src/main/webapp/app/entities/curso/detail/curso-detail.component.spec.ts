import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CursoDetailComponent } from './curso-detail.component';

describe('Curso Management Detail Component', () => {
  let comp: CursoDetailComponent;
  let fixture: ComponentFixture<CursoDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CursoDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ curso: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(CursoDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(CursoDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load curso on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.curso).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
