//
//  SideMenuController.swift
//  VegiTable
//

import Foundation
import UIKit

class ProfileTableHeader: UITableViewHeaderFooterView {
    static let id = "ProfileTableHeader"
    
    private let imageView: UIImageView = {
        let imageView = UIImageView()
        imageView.contentMode = .scaleAspectFit
        imageView.image = UIImage(named: "VegiTableLogo")
        return imageView
    }()
    
    private let label: UILabel = {
        let label = UILabel()
        label.text = "Users' Name"
        label.textColor = .white
        label.font = .systemFont(ofSize: 22)
        label.textAlignment = .center
        return label
    }()
    
    override init(reuseIdentifier: String?) {
        super.init(reuseIdentifier: reuseIdentifier)
        
        // add contents to the view
        contentView.addSubview(label)
        contentView.addSubview(imageView)
        contentView.backgroundColor = UIColor.init(named: "VT_MenuBgColor")!
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        
        // set the frame for the label
        label.sizeToFit()
        label.frame = CGRect(x: 0, y: contentView.frame.size.height-10-label.frame.size.height,
                             width: contentView.frame.size.width, height: label.frame.size.height)
        // set the frame for the image view
        imageView.frame = CGRect(x: 0, y: 0, width: contentView.frame.size.width-10,
                                 height: contentView.frame.size.height-5-label.frame.size.height)
        
    }
    
    // to be used later for functionality (user is logged in)
    func configHeader(userName: String) {
        label.text = userName
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
}

class MenuController: UITableViewController {
    // allows this clas to connect with the ContainerVC for navigation
    public var delegate: MenuControllerDelegate?
    
    init() {
        super.init(nibName: nil, bundle: nil)
        self.tableView.register(UITableViewCell.self, forCellReuseIdentifier: "cell")
    }
    
    override func viewDidLoad() {
        tableView.backgroundColor = UIColor.init(named: "VT_MenuBgColor")!
        view.backgroundColor = UIColor.init(named: "VT_MenuBgColor")!
    }
       
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    //TableView
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return menuItems.count
    }
    
    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 60
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath)
        tableView.register(ProfileTableHeader.self, forHeaderFooterViewReuseIdentifier: "ProfileTableHeader")
        
        cell.backgroundColor = UIColor.init(named: "VT_MenuBgColor")!
        if(menuItems[indexPath.row].title == "Home") {
            //set the Home item as the selected one to start
            selectMenuItem(selectedCell: cell, index: indexPath.row)
        } else {
            // add all other items as deselected items
            deselectMenuItem(cell: cell, index: indexPath.row)
        }
        
        return cell
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        // set ALL items as deselected with design and then set the selected item as the selected design
        resetMenuItems()
        selectMenuItem(selectedCell: tableView.cellForRow(at: indexPath)!, index: indexPath.row)
        
        // get the title of the selected item
        let selectedItem = menuItems[indexPath.row].title
        // find the index of the selected controller
        let index = findControllerByTitle(title: selectedItem)
        // update the view in the ContainerVC
        delegate?.updateView(index: index)
    }
    
    override func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        // dequeue the header
        let header = tableView.dequeueReusableHeaderFooterView(withIdentifier: "ProfileTableHeader") as? ProfileTableHeader
        header?.configHeader(userName: "YourNameHere")
        
        return header
    }
    
    override func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 150
    }
    
    
    private func selectMenuItem(selectedCell: UITableViewCell, index: Int) {
        // set background color for cell
        selectedCell.contentView.backgroundColor = UIColor.init(named: "VT_SelectedMenuItem")!
        
        // set image and it's size
        selectedCell.imageView?.image = menuItems[index].selectedIcon
        let imageSize = CGSize(width: 30, height: 30)
        UIGraphicsBeginImageContextWithOptions(imageSize, false, UIScreen.main.scale)
        let imageRect = CGRect.init(origin: CGPoint.zero, size: imageSize)
        selectedCell.imageView?.image?.draw(in: imageRect)
        selectedCell.imageView?.image? = UIGraphicsGetImageFromCurrentImageContext()!;
        UIGraphicsEndImageContext();
                
        // set label and the text color
        selectedCell.textLabel?.textColor = UIColor.init(named: "VT_Purple")!
        selectedCell.textLabel?.text = menuItems[index].title
    }
    
    private func deselectMenuItem(cell: UITableViewCell, index: Int) {
        // reset cell background color
        cell.contentView.backgroundColor = UIColor.init(named: "VT_MenuBgColor")!
       
        // update images to the deselected one
        cell.imageView?.image = menuItems[index].deselectedIcon
        let imageSize = CGSize(width: 30, height: 30)
        UIGraphicsBeginImageContextWithOptions(imageSize, false, UIScreen.main.scale)
        let imageRect = CGRect.init(origin: CGPoint.zero, size: imageSize)
        cell.imageView?.image?.draw(in: imageRect)
        cell.imageView?.image? = UIGraphicsGetImageFromCurrentImageContext()!;
        UIGraphicsEndImageContext();
        
        // update text color of label
        cell.textLabel?.textColor = UIColor.init(named: "VT_InputPlaceholder")!
        cell.textLabel?.text = menuItems[index].title
    }
    
    private func resetMenuItems() {
        // get list of all visible cells
        let cells = self.tableView.visibleCells
        var index = 0;
        for cell in cells {
            // loop thrugh all cells and set their design to deselected
            deselectMenuItem(cell: cell, index: index)
            index += 1
        }
    }
    
    private func findControllerByTitle(title: String) -> Int {
        // loop through all menu items except logout (last one)
        for i in 0..<menuItems.count-1 {
            if title == menuItems[i].title {
                return i
            }
        }
        return -1
    }
}
