import dayjs from 'dayjs/esm';
import { IArea } from 'app/entities/area/area.model';
import { StatusCurso } from 'app/entities/enumerations/status-curso.model';

export interface ICurso {
  id: number;
  nome?: string | null;
  descricao?: string | null;
  status?: StatusCurso | null | undefined;
  dataCriacao?: dayjs.Dayjs | null;
  dataInatividade?: dayjs.Dayjs | null;
  area?: Pick<IArea, 'id' | 'nome'> | null;
}

export type NewCurso = Omit<ICurso, 'id'> & { id: null };
