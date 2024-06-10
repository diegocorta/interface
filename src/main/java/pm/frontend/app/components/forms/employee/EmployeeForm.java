package pm.frontend.app.components.forms.employee;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.springframework.hateoas.EntityModel;
import org.springframework.util.SerializationUtils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.dom.Style.AlignItems;
import com.vaadin.flow.dom.Style.Display;
import com.vaadin.flow.dom.Style.Overflow;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.server.StreamResource;

import pm.employee.common.dto.employee.ContractTypeDto;
import pm.employee.common.dto.employee.EmployeeContractDto;
import pm.employee.common.dto.employee.EmployeeDto;
import pm.employee.common.dto.employee.GenderTypeDto;
import pm.employee.common.dto.employee.JobTypeDto;
import pm.frontend.app.components.standard.components.AlertBuilder;
import pm.frontend.app.components.standard.components.ViewType;
import pm.frontend.app.logic.CountryService;
import pm.frontend.app.logic.employee.ContractTypeService;
import pm.frontend.app.logic.employee.EmployeeService;
import pm.frontend.app.logic.employee.EmployeeServicesWrapper;
import pm.frontend.app.logic.employee.GenderTypeService;
import pm.frontend.app.logic.employee.JobTypeService;
import pm.frontend.app.logic.security.UserService;
import pm.security.v2.common.dto.min.UserMinDto;

public class EmployeeForm extends VerticalLayout {
	
	private static final long serialVersionUID = -5998011063509148661L;
	
	private CountryService countryService;
	private EmployeeService employeeService;
	private ContractTypeService contractTypeService;
	private JobTypeService jobTypeService;
	private GenderTypeService genderTypeService;
	private UserService userService;
	
	private EntityModel<EmployeeDto> employee;
	private EntityModel<EmployeeContractDto> employeeContract;
	
	private EntityModel<EmployeeDto> employeeInit;
	private EntityModel<EmployeeContractDto> employeeContractInit;
	
	private MainProfile mainProfile;
	
    private Binder<EmployeeDto> employeeBinder;
    private Binder<EmployeeContractDto> employeeContractBinder;
    
    private ViewType viewType;
    
    private Collection<EntityModel<ContractTypeDto>> contractTypeList;
    private Collection<EntityModel<JobTypeDto>> jobTypeList;
    private Collection<EntityModel<GenderTypeDto>> genderTypeList;
    private Collection<EntityModel<UserMinDto>> userMinList;
    
    private byte[] userImage;
    private String userMimeType;

    
	public EmployeeForm(
			CountryService countryService,
			EmployeeServicesWrapper employeeServicesWrapper,
			UserService userService,
			ViewType viewType) {

		this.countryService = countryService;
		this.employeeService = employeeServicesWrapper.getEmployeeService();
		this.contractTypeService = employeeServicesWrapper.getContractTypeService();
		this.jobTypeService = employeeServicesWrapper.getJobTypeService();
		this.genderTypeService = employeeServicesWrapper.getGenderTypeService();
		this.userService = userService;
		
		this.viewType = viewType;
		
		this.employeeBinder = new Binder<>(EmployeeDto.class);
	    this.employeeContractBinder = new Binder<>(EmployeeContractDto.class);
		
		setSizeFull();
		setPadding(false);
		
		TabSheet tabSheet = new TabSheet();
		
		tabSheet.setMaxWidth("100%");
		tabSheet.setWidth("100%");
		tabSheet.setMaxHeight("100%");
	    
		mainProfile = new MainProfile();
		
		tabSheet.add("Perfil", mainProfile);
		tabSheet.add("Direcciones", new LazyComponent(
                () -> new VerticalLayout()));
		tabSheet.add("Calendario", new LazyComponent(
                () -> new VerticalLayout()));
		tabSheet.add("Documentación", new LazyComponent(
                () -> new VerticalLayout()));
		
        add(tabSheet);

	}
	
	public EmployeeForm(CountryService countryService,
			EmployeeServicesWrapper employeeServicesWrapper,
			UserService userService,
			EntityModel<EmployeeDto> employeeDto,
			ViewType viewType) {

		this(countryService, employeeServicesWrapper, userService, viewType);
		
		this.employee = employeeDto;
		employeeInit = EntityModel.of(SerializationUtils.clone(employee.getContent()));
		employeeInit.add(employee.getLinks());
		
		this.employeeContract = null;
		this.employeeContractInit = null;
		
		loadRelated();
		
		mainProfile.load();

	}
	
	public void setViewType(ViewType viewType) {
		
		this.viewType = viewType;
		
		mainProfile.setViewType(viewType);
	}
	
	public void loadData(EntityModel<EmployeeDto> employeeDto) {
		
		if (employeeBinder != null) employeeBinder.removeBean();
		if (employeeContractBinder != null) employeeContractBinder.removeBean();
		
		this.employeeBinder = new Binder<>(EmployeeDto.class);
		employeeBinder.setBean(new EmployeeDto());
		
	    this.employeeContractBinder = new Binder<>(EmployeeContractDto.class);
		employeeContractBinder.setBean(new EmployeeContractDto());

		
		this.employee = employeeDto;
		employeeInit = EntityModel.of(SerializationUtils.clone(employee.getContent()));
		employeeInit.add(employee.getLinks());
		
		this.employeeContract = null;
		this.employeeContractInit = null;
		
		loadRelated();
		
		mainProfile.load();
				
	}
	
	public void loadNew() {
		
		if (employeeBinder != null) employeeBinder.removeBean();
		if (employeeContractBinder != null) employeeContractBinder.removeBean();
		
		this.employeeBinder = new Binder<>(EmployeeDto.class);
		employeeBinder.setBean(new EmployeeDto());
		
		this.employeeContractBinder = new Binder<>(EmployeeContractDto.class);
		employeeContractBinder.setBean(new EmployeeContractDto());

	    
		this.employee = null;
		this.employeeInit = null;
		
		this.employeeContract = null;
		this.employeeContractInit = null;

		loadRelated();
		
		mainProfile.load();
				
	}
	
	private void loadRelated() {
		
		if (contractTypeList == null) {
			contractTypeList = contractTypeService.getAllContracts().getContent();
		}
		if (jobTypeList == null) {
			jobTypeList = jobTypeService.getAllJobs().getContent();
		}
		if (genderTypeList == null) {
			genderTypeList = genderTypeService.getAllGenders().getContent();
		}
		if (userMinList == null) {
			userMinList = userService.getAllUsersMinified().getContent();
		}
		
	}
	
	private class LazyComponent extends Div {
		
        private static final long serialVersionUID = 8854973147025065218L;

		public LazyComponent(
                SerializableSupplier<? extends Component> supplier) {
            addAttachListener(e -> {
                if (getElement().getChildCount() == 0) {
                    add(supplier.get());
                }
            });
        }
    }
	
	private class MainProfile extends VerticalLayout {
		
		private static final long serialVersionUID = -1387120031740672729L;

		private Avatar avatar = new Avatar();
		private H3 fullname = new H3();

		MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
		Upload upload = new Upload(buffer);
		
		Details basicInfo = new Details("Información básica");
		private FormLayout basicInfoForm;
		Details contractInfo = new Details("Información de contrato actual");
		private FormLayout contractInfoForm;
		
		private TextField name = new TextField();
		private TextField firstSurname = new TextField();
		private TextField lastSurname = new TextField();
		private TextField dni = new TextField();
		private TextField mail = new TextField();
		
		private ComboBox<String> countriesCombo = new ComboBox<>();
		private DatePicker birthdate = new DatePicker();
		private ComboBox<EntityModel<GenderTypeDto>> genderCombo = new ComboBox<>();
		private TextField naf = new TextField();
		private ComboBox<EntityModel<UserMinDto>> userCombo = new ComboBox<>(); 
		private Button password = new Button("Cambiar contraseña");

		private ComboBox<EntityModel<ContractTypeDto>> contractType = new ComboBox<>();
		private DatePicker startDate = new DatePicker();
		private DatePicker endDate = new DatePicker();
		private ComboBox<EntityModel<JobTypeDto>> jobType = new ComboBox<>();
		private NumberField weekHours = new NumberField();
		private Button newContract = new Button("Nuevo contrato");
		
		private boolean newContractFlag = false;

		
		public MainProfile() {
			
			setSizeFull();
			setPadding(false);
			setSpacing(false);
			getStyle().setDisplay(Display.FLEX);
			getStyle().setAlignItems(AlignItems.CENTER);
			
			avatar.getStyle().setWidth("11rem");
			avatar.getStyle().setHeight("11rem");
			
			fullname.getStyle().setPadding("5px");
			
			UploadImgI18N uii = new UploadImgI18N();
			int maxFileSizeInBytes = 65535; // 64KB
			
			upload.setI18n(uii);
			upload.setMaxFileSize(maxFileSizeInBytes);
			upload.setAcceptedFileTypes("image/jpeg", "image/png");
			upload.setMaxFiles(1);
			upload.addSucceededListener(event -> {
			    
			    byte[] image = null;
				try {
					image = buffer.getInputStream(event.getFileName()).readAllBytes();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    event.getMIMEType();
			    
			    updateEmployeeImage(image, event.getMIMEType());
			    
			    upload.clearFileList();
			});
			upload.addFileRejectedListener(event -> {
				String errorMessage = event.getErrorMessage();
				AlertBuilder.warn("Error subiendo archivos", null, errorMessage).open();
			});
			upload.getStyle().setOverflow(Overflow.VISIBLE);
			upload.getStyle().setMargin("5px");
			upload.getStyle().setPadding("5px");
			
			
			basicInfoForm = new FormLayout();
			contractInfoForm = new FormLayout();
			
			name.setLabel("Nombre");
			firstSurname.setLabel("Primer apellido");
			lastSurname.setLabel("Segundo Apellido");
			
			dni.setLabel("DNI/NIF");
			mail.setLabel("Email");
			
			countriesCombo.setLabel("Nacionalidad");
			List<String> countries = countryService.getAllCountries();
			countriesCombo.setItems(countries);
//			countriesCombo.setValue("España");
			
			genderCombo.setLabel("Genero");
			genderCombo.setItemLabelGenerator( ug -> ug.getContent().getName());
//			genderCombo.setValue("Masculino");
			
			userCombo.setLabel("Nombre de usuario");
			userCombo.setItemLabelGenerator( ug -> ug.getContent().getUsername());
			
			birthdate.setLabel("Fecha de nacimiento");
//			birthdate.setValue(LocalDate.of(1994, 10, 12));
			
			naf.setLabel("Nº seguridad social");
//			naf.setValue("111111111111");
			
			password.getStyle().setMarginTop("16px");
			
			basicInfo.setOpened(true);
			basicInfo.setWidthFull();
			basicInfo.add(basicInfoForm);
			
			basicInfoForm.add(name, firstSurname, lastSurname, userCombo, mail,
					genderCombo, birthdate, countriesCombo, dni, naf, jobType, password);
			
			basicInfoForm.setResponsiveSteps(
	                // Use one column by default
	                new ResponsiveStep("0", 1),
	                // Use two columns, if layout's width exceeds 500px
	                new ResponsiveStep("300px", 2),
	                new ResponsiveStep("600px", 3));
			
			contractType.setLabel("Tipo de contrato");
			contractType.setItemLabelGenerator( ug -> ug.getContent().getName());
			
			startDate.setLabel("Comienzo de contrato");
			endDate.setLabel("Fin de contrato");
			
			jobType.setLabel("Tipo de trabajo");
			jobType.setItemLabelGenerator( ug -> ug.getContent().getName());
			
			weekHours.setLabel("Horas semanales");
			weekHours.setStep(0.5);
			weekHours.setStepButtonsVisible(true);
			
			contractInfo.setOpened(true);
			contractInfo.setWidthFull();
			contractInfo.add(contractInfoForm);
			contractInfo.getStyle().setPaddingBottom("10px");
			
			newContract.getStyle().setMarginTop("16px");

			
			contractInfoForm.add(contractType, startDate, endDate, weekHours, newContract);
			
			contractInfoForm.setResponsiveSteps(
	                // Use one column by default
	                new ResponsiveStep("0", 1),
	                // Use two columns, if layout's width exceeds 500px
	                new ResponsiveStep("300px", 2),
	                new ResponsiveStep("600px", 3));
			
			setViewType(viewType);
			
			newContract.addClickListener(click -> {
				
				if (employeeContractInit != null) {
					if (contractType.isEnabled() && employeeContractBinder.validate().isOk()) {
						
						EmployeeContractDto newEmployeeContract = new EmployeeContractDto();
						employeeContractBinder.writeBeanIfValid(newEmployeeContract);
						
						if (newEmployeeContract.getContractEnd() == null) {
							AlertBuilder.warn("Advertencia", null, "El contrato actual aun no ha terminado, debes finalizar el actual para poder crear uno nuevo").open();
						} else {
							newContractFlag = true;
							
							if (employeeContractBinder != null) employeeContractBinder.removeBean();
							
							employeeContractBinder.setBean(new EmployeeContractDto());
							
							employeeContract = null;
							employeeContractInit = null;
						}
						
					}
				} else {
					newContractFlag = true;
				}
				
				weekHours.setEnabled(true);
				startDate.setEnabled(true);
				endDate.setEnabled(true);
				contractType.setEnabled(true);
			});
			
			add(avatar, fullname, upload, basicInfo, contractInfo);
		}
		
		public void setViewType(ViewType viewType) {
			
			boolean readOnlyUser;
			boolean readOnlyContract;
			
			if (viewType.equals(ViewType.VIEW)) {
				readOnlyUser = true;
				readOnlyContract = true;

				upload.setVisible(false);
				newContract.setEnabled(false);

				
			} else if (viewType.equals(ViewType.NEW)) {
				readOnlyUser = false;
				readOnlyContract = false;
				
				weekHours.setEnabled(false);
				startDate.setEnabled(false);
				endDate.setEnabled(false);
				contractType.setEnabled(false);
				
				upload.setVisible(true);
				newContract.setEnabled(true);

				
			} else {
				readOnlyUser = false;
				readOnlyContract = false;
				
				upload.setVisible(true);
				newContract.setEnabled(true);
			}
			
			name.setReadOnly(readOnlyUser);
			firstSurname.setReadOnly(readOnlyUser);
			lastSurname.setReadOnly(readOnlyUser);
			userCombo.setReadOnly(readOnlyUser);
			dni.setReadOnly(readOnlyUser);
			naf.setReadOnly(readOnlyUser);
			birthdate.setReadOnly(readOnlyUser);
			mail.setReadOnly(readOnlyUser);
			genderCombo.setReadOnly(readOnlyUser);
			countriesCombo.setReadOnly(readOnlyUser);
			jobType.setReadOnly(readOnlyUser);
			
			weekHours.setReadOnly(readOnlyContract);
			startDate.setReadOnly(readOnlyContract);
			endDate.setReadOnly(readOnlyContract);
			contractType.setReadOnly(readOnlyContract);	

		}
		
		private void updateEmployeeImage(byte[] image, String mimeType) {
			
			// Create a StreamResource from the byte array
			if (image != null) {
				
				userImage = image;
				userMimeType = mimeType;
				
		        StreamResource resource = new StreamResource("avatar.png", () -> {
		            return new ByteArrayInputStream(image);
		        });
		
		        // Set the image as the avatar image
		        avatar.setImageResource(resource);
			}
		}
		
		public void load() {
						
			jobType.clear();
			jobType.setItems(jobTypeList);
			genderCombo.clear();
			genderCombo.setItems(genderTypeList);
			contractType.clear();
			contractType.setItems(contractTypeList);
			userCombo.clear();
			userCombo.setItems(userMinList);

			
			employeeBinder.forField(name)
				.asRequired("El empleado debe tener un nombre")
				.withValidator(name -> name.length() >= 3 && name.length() <= 20,
						"El nombre del empleado debe tener entre 3 y 20 caracteres")
				.bind(EmployeeDto::getName, EmployeeDto::setName);
			
			employeeBinder.forField(firstSurname)
				.asRequired("El empleado debe tener un apellido")
				.withValidator(firstSurname -> firstSurname.length() >= 2 && firstSurname.length() <= 40,
						"El apellido del empleado debe tener entre 2 y 40 caracteres")
				.bind(EmployeeDto::getFirstSurname, EmployeeDto::setFirstSurname);
			
			employeeBinder.forField(lastSurname)
				.withValidator(lastSurname -> StringUtils.isEmpty(lastSurname) || (lastSurname.length() <= 40),
						"El apellido del empleado debe tener maximo 40 caracteres")
				.bind(EmployeeDto::getLastSurname, EmployeeDto::setLastSurname);
			
			
			employeeBinder.forField(mail)
				.asRequired("El empleado debe tener email")
				.withValidator(new EmailValidator("Formato de email incorrecto"))
				.bind(EmployeeDto::getEmail, EmployeeDto::setEmail);
		

			employeeBinder.forField(birthdate)
				.asRequired("El empleado debe tener fecha de nacimiento")
				.withValidator(birthdate -> birthdate.isBefore(LocalDate.now().plusDays(1)),
						"La fecha no puede estar en el futuro")
				.withValidator(birthdate -> birthdate.isAfter(LocalDate.now().minusYears(120)),
						"Fecha invalida")
				.bind(employee -> {
					if (employee.getBirthdate() != null) {
						return LocalDate.parse(employee.getBirthdate());
					} else {
						return null;
					}
				},
						(employee, birthdate) -> { employee.setBirthdate(birthdate.format(DateTimeFormatter.ISO_DATE)); 
				});
			
			employeeBinder.forField(dni)
				.asRequired("El empleado debe tener un DNI")
				.withValidator(name -> name.length() >= 8 && name.length() <= 15,
						"El dni del empleado debe tener entre 8 y 15 caracteres")
				.bind(EmployeeDto::getCardId, EmployeeDto::setCardId);

			
			employeeBinder.forField(naf)
				.asRequired("El empleado debe tener un numero de la seguridad social")
				.withValidator(name -> name.length() >= 8 && name.length() <= 15,
						"El numero del empleado debe tener entre 8 y 15 caracteres")
				.bind(EmployeeDto::getNafId, EmployeeDto::setNafId);
			
			
			employeeBinder.forField(countriesCombo)
				.asRequired("El empleado debe tener una nacionalidad")
				.bind(employee -> {
					if (employee.getCountry() != null) {
						return countryService.getLocaleCountry(employee.getCountry());
					} else {
						return null;
					} 
				},
					(employee, country) -> employee.setCountry(countryService.getISOCode(country)));
			
			
			employeeBinder.forField(jobType)
				.asRequired("El empleado debe tener un tipo de trabajo")
				.bind(thisemployee -> {
					if (employee != null) {
						return employeeService.getJobOfEmployee(employee);
					} else {
						return null;
					} 
				}, 
						(employee, job) -> employee.setJob(job.getContent().getName()));
			
			
			employeeBinder.forField(genderCombo)
				.asRequired("El empleado debe tener un genero")
				.bind(thisemployee ->  {
					if (employee != null) {
						return employeeService.getGenderOfEmployee(employee);
					} else {
						return null;
					}
				},
						(employee, job) -> employee.setGender(job.getContent().getName()));
			
			
			employeeBinder.forField(userCombo)
				.asRequired("El empleado debe tener un nombre de usuario asociado")
				.bind(thisemployee -> {
					if (employee != null) {
						return userMinList.stream()
								.filter(um -> um.getContent().getId().equals(employee.getContent().getSecurityUserId()))
								.findFirst()
								.get();
					} else {
						return null;
					}
				}, 
						(employee, security) -> employee.setSecurityUserId(security.getContent().getId()));			
			
			employeeContractBinder.forField(startDate)
				.asRequired("El contrato debe tener fecha de inicio")
				.withValidator(startdate -> startdate.isAfter(LocalDate.now().minusYears(120)),
						"Fecha invalida")
				.bind(contract -> {
					if (contract.getContractStart() != null) {
						return LocalDate.parse(contract.getContractStart());
					} else {
						return null;
					}
				},
						(contract, startdate) -> { contract.setContractStart(startdate.format(DateTimeFormatter.ISO_DATE)); 
				});
			
			
			employeeContractBinder.forField(endDate)
				.withValidator(enddate -> {
					if (enddate != null) return enddate.isAfter(LocalDate.now().minusYears(120));
					return true;
					},
						"Fecha invalida")
				.withValidator(enddate -> {
					if (enddate != null) return enddate.isAfter(startDate.getValue());
					return true;
				},
						"Fecha invalida")
				.bind(contract -> {
						if (contract.getContractEnd() != null) {
							return LocalDate.parse(contract.getContractEnd());
						} else {
							return null;
						}
					},
						(contract, enddate) -> { contract.setContractEnd((enddate != null) ? enddate.format(DateTimeFormatter.ISO_DATE) : null); 
				});
			
			
			employeeContractBinder.forField(weekHours)
				.asRequired("El contrato debe tener estipuladas unas horas")
				.withValidator(hour -> hour >= 0,
						"Las horas no pueden ser negativas")
				.withValidator(hour -> hour < 100,
						"Las horas no pueden mas de 100")
				.bind(source -> {
					if (source.getWeekWorkHours() != null) {
						return source.getWeekWorkHours().doubleValue();
					} else {
						return null;
					}
				}, (source, value) -> source.setWeekWorkHours(value.longValue()));
			
			
			employeeContractBinder.forField(contractType)
				.asRequired("El contrato debe ser de algun tipo")
				.bind(thiscontract -> {
					if (thiscontract.getContractTypeId() != null) {
						return contractTypeList.stream()
								.filter(ct -> ct.getContent().getId().equals(thiscontract.getContractTypeId()))
								.findFirst()
								.get(); 
					} else {
						return null;
					}
				},
					(contract, type) -> contract.setContractTypeId(type.getContent().getId()));
			
			
			if (employee != null) {
				
				String fullString = WordUtils.capitalize(employee.getContent().getFullName());
				
				avatar.setName(fullString);
				if (employee.getContent().getImage() != null) {
					// Create a StreamResource from the byte array
			        StreamResource resource = new StreamResource("avatar.png", () -> {
			            return new ByteArrayInputStream(employee.getContent().getImage());
			        });
			
			        // Set the image as the avatar image
			        avatar.setImageResource(resource);
				}
				
				fullname.setText(fullString);
				
				employeeContract = employeeService.getLastContractOfEmployee(employee);
				
				if (employeeContract != null) {
					
					employeeContractInit = EntityModel.of(SerializationUtils.clone(employeeContract.getContent()));
					employeeContractInit.add(employeeContract.getLinks());
					
					employeeContractBinder.setBean(employeeContract.getContent());	

				}
				
				employeeBinder.setBean(employee.getContent());			
				
			} else {
				
				avatar.setName("");
				avatar.setImageResource(null);
				
				fullname.setText("");
				fullname.setVisible(false);	
				
				newContract.setEnabled(false);
			
			}			
		}
	}
	
	public void save() {
		
		if (viewType.equals(ViewType.NEW)) {
			
			EmployeeDto newEmployee = new EmployeeDto();
			EmployeeContractDto employeeContract = null;
			boolean error = false;
			
			if(employeeBinder.validate().isOk()) {
				employeeBinder.writeBeanIfValid(newEmployee);

				if (userImage != null) {
					newEmployee.setImage(userImage);
					newEmployee.setImageType(userMimeType);
				}
				
			} else {
				error = true;
			}
			
			if (mainProfile.contractType.isEnabled()) {
				employeeContract = new EmployeeContractDto();
				if (employeeContractBinder.validate().isOk()) {
					employeeContractBinder.writeBeanIfValid(employeeContract);
				} else {
					error = true;
				}
				
			}
			
			if (!error) {
				
				var employee = employeeService.createEmployee(newEmployee);
				AlertBuilder.success(null, "Empleado", "creado correctamente").open();
				
				if (employeeContract != null) {
					employeeContract.setEmployeeId(employee.getContent().getId());
					
					employeeService.createEmployeeContract(employeeContract);
					AlertBuilder.success(null, "Contrato", "creado correctamente").open();
					
				}
				
				
			}
			
		} else if (viewType.equals(ViewType.EDIT)) {
			
			boolean error = false;
			boolean employeeChanged = false;
			boolean employeeContractChanged = false;
			boolean employeeContractIsNew = false;
			EmployeeContractDto oldEmployeeContract = new EmployeeContractDto();

			
			if(employeeBinder.validate().isOk()) {
				employeeBinder.writeBeanIfValid(employee.getContent());
				
				if (userImage != null) {
					employee.getContent().setImage(userImage);
					employee.getContent().setImageType(userMimeType);
				}
				
				if (!employee.getContent().equals(employeeInit.getContent())) {
					employeeChanged = true;
					
					
				}
				
			} else {
				error = true;
			}
			
			if(employeeContractInit != null && employeeContractBinder.validate().isOk()) {
				employeeContractBinder.writeBeanIfValid(oldEmployeeContract);
				
				if (!oldEmployeeContract.equals(employeeContractInit.getContent())) {
					employeeContractChanged = true;
				}
				
			} else if (employeeContractInit == null) {
				
				if (mainProfile.newContractFlag && employeeContractBinder.validate().isOk()) {
					employeeContractBinder.writeBeanIfValid(employeeContract.getContent());
					employeeContract.getContent().setEmployeeId(employee.getContent().getId());
					employeeContractIsNew = true;
					
				} else if (mainProfile.newContractFlag) {
					error = true;
				}	
			} else {
				error = true;
			}
			
			if (!error) {
				
				if (employeeChanged) {
					
					employeeService.updateEmployee(employee);
					AlertBuilder.success(null, "Empleado", "actualizado correctamente").open();
				}
				if (employeeContractChanged) {
					
					employeeService.updateEmployeeContract(employeeContract);
					AlertBuilder.success(null, "Contrato", "actualizado correctamente").open();
				} else if (employeeContractIsNew) {
					
					employeeService.createEmployeeContract(employeeContract.getContent());
					AlertBuilder.success(null, "Contrato", "creado correctamente").open();

				}
			}
			
//			if (mainProfile.contractType.isEnabled()) {
//				employeeContract = new EmployeeContractDto();
//				if (employeeContractBinder.validate().isOk()) {
//					employeeContractBinder.writeBeanIfValid(employeeContract);
//				} else {
//					error = true;
//				}
//				
//			}
			
//			if (!error) {
//				
//				var employee = employeeService.createEmployee(newEmployee);
//				AlertBuilder.success(null, "Empleado", "creado correctamente").open();
//				
//				if (employeeContract != null) {
//					employeeContract.setEmployeeId(employee.getContent().getId());
//					
//					employeeService.createEmployeeContract(employeeContract);
//					AlertBuilder.success(null, "Contrato", "creado correctamente").open();
//				}
//				
//				
//			}
			
		}

	}
	
	public void delete() {
		
		AlertBuilder.info(null, null, "Actualmente no se pueden borrar empleados").open();
	}
	
	/**
	 * Provides a default I18N configuration for the Upload examples
	 *
	 * At the moment the Upload component requires a fully configured I18N instance,
	 * even for use-cases where you only want to change individual texts.
	 *
	 * This I18N configuration is an adaption of the web components I18N defaults
	 * and can be used as a basis for customizing individual texts.
	 */
	private class UploadImgI18N extends UploadI18N {
		
	    private static final long serialVersionUID = 5960935435285717532L;

		public UploadImgI18N() {
	        setDropFiles(new DropFiles().setOne("Arrastra archivo aquí")
	                .setMany("Arrastra archivo aquí"));
	        setAddFiles(new AddFiles().setOne("Seleccionar archivo...")
	                .setMany("Seleccionar archivos..."));
	        setError(new Error().setTooManyFiles("Demasiados archivos.")
	                .setFileIsTooBig("El archivo no puede tener mas de 64KB")
	                .setIncorrectFileType("Formato de archivo incorrecto. Solo se pueden subir imagenes JPG o PNG"));
	    }
	}
	
}
